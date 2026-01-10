# Lekhani - Indic Language Keyboards

Multi-language keyboard suite for Indian scripts. Each language is built from a shared codebase using Gradle product flavors.

## Supported Languages

| Language | Script | App ID |
|----------|--------|--------|
| Sanskrit | Devanagari | `in.dharmaposhanam.lekhani.sanskrit` |
| Hindi | Devanagari | `in.dharmaposhanam.lekhani.hindi` |
| Telugu | Telugu | `in.dharmaposhanam.lekhani.telugu` |
| Kannada | Kannada | `in.dharmaposhanam.lekhani.kannada` |

## Building

Build all flavors:
```bash
./gradlew assembleDebug
```

Build specific flavor:
```bash
./gradlew assembleSanskritDebug
./gradlew assembleHindiDebug
./gradlew assembleTeluguDebug
./gradlew assembleKannadaDebug
```

Build release (requires signing configuration):
```bash
./gradlew assembleSanskritRelease
```

## Project Structure

```
lekhani/
├── app/src/
│   ├── main/                    # Shared code (90%)
│   │   ├── java/.../lekhani/
│   │   │   ├── LekhaniInputMethodService.kt
│   │   │   ├── LekhaniKeyboardView.kt
│   │   │   ├── KeyboardTypes.kt
│   │   │   ├── SettingsActivity.kt
│   │   │   └── ImeSettingsActivity.kt
│   │   └── res/                 # Shared resources
│   │
│   ├── sanskrit/                # Sanskrit flavor
│   │   ├── java/.../lekhani/KeyboardLayout.kt
│   │   └── res/
│   │
│   ├── hindi/                   # Hindi flavor
│   │   ├── java/.../lekhani/KeyboardLayout.kt
│   │   └── res/
│   │
│   ├── telugu/                  # Telugu flavor
│   │   ├── java/.../lekhani/KeyboardLayout.kt
│   │   └── res/
│   │
│   └── kannada/                 # Kannada flavor
│       ├── java/.../lekhani/KeyboardLayout.kt
│       └── res/
```

## Adding a New Language

1. Add product flavor in `app/build.gradle`
2. Create `app/src/<language>/java/in/dharmaposhanam/lekhani/KeyboardLayout.kt`
3. Create `app/src/<language>/res/values/strings.xml`
4. Create `app/src/<language>/res/xml/method.xml`

## Keyboard Layout

All keyboards use the InScript layout standard, matching iOS keyboard behavior for long-press alternates.

## License

Copyright (c) Dharma Poshanam. All rights reserved.
