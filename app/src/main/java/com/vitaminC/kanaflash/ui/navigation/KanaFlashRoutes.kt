package com.vitaminC.kanaflash.ui.navigation

object KanaFlashRoutes {
    const val HOME = "home"
    const val DECKS = "decks"
    const val DECK_DETAIL = "decks/{deckId}"
    const val FLASHCARDS = "flashcards"
    const val QUIZ = "quiz"
    const val WRITE = "write"
    const val RESULT = "result"
    const val RESULT_WITH_ARGS = "result/{score}/{total}"

    fun deckDetailRoute(deckId: Long): String {
        return "decks/$deckId"
    }

    fun resultRoute(score: Int, total: Int): String {
        return "result/$score/$total"
    }
}
