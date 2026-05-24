# KanaFlash

KanaFlash is a lightweight Android app for learning Japanese vocabulary. It lets users create vocabulary decks, save Japanese text with Romaji and optional meanings, and practise through flashcards, quizzes, and handwriting exercises.

The app is local-first, so decks and vocabulary entries are stored on the device using Room Database and can be accessed without an internet connection.

## Features

- Create, rename, and delete vocabulary decks
- Add, edit, and delete vocabulary entries inside each deck
- View deck word counts for easier organization
- Review vocabulary with flashcards and reveal answers when ready
- Test recall with multiple-choice quiz mode
- Practise writing Japanese characters with a touch-based writing canvas
- Use deck filtering to study one deck or all decks
- Store data locally with Room Database

## Tech Stack

- Kotlin
- Android Studio
- Jetpack Compose
- Material 3
- Navigation Compose
- Android ViewModel
- Kotlin Coroutines, Flow, and StateFlow
- Room Database
- Gradle

## Architecture

KanaFlash follows a layered MVVM-style architecture:

- UI layer: Jetpack Compose screens and reusable UI components
- ViewModel layer: manages screen state, selected decks, and user actions
- Repository layer: centralizes access to deck and vocabulary data
- DAO layer: defines Room database operations
- Database layer: stores decks and vocabulary entries locally

The database uses two main entities:

- `Deck`: stores deck titles
- `VocabularyEntry`: stores Romaji, Japanese text, optional meaning, and the related deck ID

Each deck can contain many vocabulary entries. Deleting a deck also removes its related vocabulary entries through cascade deletion.

## Main Screens

- Home: shows a rotating deck preview and quick access to study features
- Decks: manages vocabulary decks
- Vocabulary: manages words inside a selected deck
- Flashcards: supports step-by-step review with answer reveal
- Quiz: generates multiple-choice questions from saved vocabulary
- Write Mode: provides a handwriting canvas with undo, clear, reveal, and navigation controls

## Getting Started

1. Clone or download the project.
2. Open the project folder in Android Studio.
3. Let Gradle sync the project dependencies.
4. Run the app on an Android emulator or physical Android device.

## Build Requirements

- Android Studio
- Android SDK with compile SDK 36 support
- JDK 11 or newer
- Gradle wrapper included in the project

## Future Improvements

- Add search and sorting for vocabulary entries
- Support bulk vocabulary import from structured text or JSON
- Improve writing practice with guide lines, hints, or stroke templates
- Add handwriting recognition for automatic answer checking
- Add cloud backup or multi-device sync support
