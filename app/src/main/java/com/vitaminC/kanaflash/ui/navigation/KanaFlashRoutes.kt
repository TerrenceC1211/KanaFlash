package com.vitaminC.kanaflash.ui.navigation

object KanaFlashRoutes {
    const val HOME = "home"
    const val VOCABULARY = "vocabulary"
    const val FLASHCARDS = "flashcards"
    const val QUIZ = "quiz"
    const val RESULT = "result"
    const val RESULT_WITH_ARGS = "result/{score}/{total}"

    fun resultRoute(score: Int, total: Int): String {
        return "result/$score/$total"
    }
}
