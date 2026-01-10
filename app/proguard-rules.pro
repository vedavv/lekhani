# Add project specific ProGuard rules here.

# Keep keyboard layout classes
-keep class in.dharmaposhanam.lekhani.KeyboardLayouts { *; }
-keep class in.dharmaposhanam.lekhani.PopupMappings { *; }
-keep class in.dharmaposhanam.lekhani.SpecialKey { *; }

# Keep IME service
-keep class in.dharmaposhanam.lekhani.LekhaniInputMethodService { *; }
-keep class in.dharmaposhanam.lekhani.LekhaniKeyboardView { *; }
