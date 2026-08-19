package `in`.dharmaposhanam.lekhani

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

/**
 * Exports a bundled font out of the APK so it can be installed system-wide or
 * carried to another device.
 *
 * Text typed in the VijayaDV PUA encoding is unreadable anywhere the font is not
 * installed, so shipping the font inside the keyboard is only half the job -
 * users need a copy they can hand to their desktop, their publisher, or another
 * app.
 *
 * The file is copied from assets; nothing is fetched over the network.
 */
object FontExporter {

    private const val EXPORT_FILE_NAME = "VijayaDV.ttf"
    private const val MIME_TYPE = "font/ttf"
    private const val CACHE_DIR = "fonts"

    sealed class Result {
        /** Written straight into the public Downloads collection. */
        object SavedToDownloads : Result()

        /** Handed to a chooser for the user to file where they want. */
        object Shared : Result()

        data class Failed(val cause: Exception) : Result()
    }

    fun exportVijayaDv(context: Context): Result = export(context, SvaraEncoding.VIJAYADV.fontAsset)

    private fun export(context: Context, assetName: String): Result {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveToDownloads(context, assetName)
                Result.SavedToDownloads
            } else {
                shareViaProvider(context, assetName)
                Result.Shared
            }
        } catch (e: Exception) {
            Result.Failed(e)
        }
    }

    /**
     * API 29+: write into MediaStore Downloads. Needs no storage permission,
     * which matters for a keyboard - an IME asking for storage access reads as
     * hostile regardless of intent.
     */
    private fun saveToDownloads(context: Context, assetName: String) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, EXPORT_FILE_NAME)
            put(MediaStore.Downloads.MIME_TYPE, MIME_TYPE)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("Downloads collection rejected the insert")

        resolver.openOutputStream(uri).use { output ->
            requireNotNull(output) { "Could not open $uri for writing" }
            context.assets.open("fonts/$assetName.ttf").use { it.copyTo(output) }
        }

        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }

    /**
     * API 24-28: MediaStore Downloads does not exist, and the legacy path would
     * need WRITE_EXTERNAL_STORAGE. Share through a FileProvider instead so the
     * manifest stays permission-free on every supported API level.
     */
    private fun shareViaProvider(context: Context, assetName: String) {
        val cacheDir = File(context.cacheDir, CACHE_DIR).apply { mkdirs() }
        val outFile = File(cacheDir, EXPORT_FILE_NAME)

        context.assets.open("fonts/$assetName.ttf").use { input ->
            outFile.outputStream().use { input.copyTo(it) }
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outFile
        )

        val share = Intent(Intent.ACTION_SEND).apply {
            type = MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Launched from an InputMethodService, so there is no activity task to join
        val chooser = Intent.createChooser(share, context.getString(R.string.download_vijayadv_font))
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        context.startActivity(chooser)
    }
}
