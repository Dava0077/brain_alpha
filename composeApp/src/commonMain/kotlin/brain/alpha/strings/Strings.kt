package brain.alpha.strings

import brain.alpha.model.shared.Difficulty

object Strings {

    private val translations = mapOf(
        "es" to mapOf(
            //GENERAL
            "play" to "JUGAR",
            "play_again" to "JUGAR DE NUEVO",
            "restart" to "REINICIAR",
            "charging" to "CARGANDO...",
            "press_play" to "Presiona Jugar",
            "you_won" to "Has ganado: ",
            "time" to "Tiempo:",
            "final_time" to "Tiempo final: ",
            "remaining_time" to "Tiempo restante: ",
            "back" to "⬅ Volver",
            "instructions" to "Instrucciones",
            "easy" to "Fácil",
            "medium" to "Medio",
            "hard" to "Difícil",
            "your_score" to "Puntuación: ",
            "neurons" to "\uD83C\uDF40 Neuronas",
            //HOME SCREEN
            "hi" to "Hola, ",
            "categories" to "CATEGORIAS",
            "memory" to "MEMORIA",
            "logic" to "LOGICA",
            "concentration" to "CONCENTRACIÓN",
            "mental_agility" to "AGILIDAD MENTAL",
            //LOG OUT
            "log_out" to "Cerrar sesión",
            "you_sure" to "¿Estás seguro de que deseas cerrar sesión?",
            "successful" to "Sesión cerrada correctamente.",
            "yes" to "Sí",
            "cancel" to "Cancelar",
            //SETTINGS SCREEN
            "settings_title" to "Ajustes",
            "preferences" to "Preferencias",
            "language" to "Idioma",
            "account_profile" to "Cuenta y perfil",
            "change_name" to "Cambiar nombre",
            "change_email" to "Cambiar email",
            "change_password" to "Cambiar contraseña",
            //COUPLES SCREEN
            "title_couples" to "JUEGO DE PAREJAS",
            "correct_couples" to "Parejas correctas:",
            "incorrect_couples" to "Parejas incorrectas:",
            "instructions" to "Instrucciones",
            "start_game" to "Empezar juego",
            "insC1" to "Debes ir seleccionando las diferentes ",
            "insC2" to "parejas",
            "insC3" to " que hay en el tablero.",
            "insC4" to "Cuento menos tiempo tardes ",
            "insC5" to "MEJOR!!",
            "insC6" to "Concéntrate, ¡y buena suerte!",
            "insC7" to "Información de la dificultad:\t\n - Fácil: 8 parejas\n - Medio: 12 parejas\n - Dificil: 16 parejas",
            //SIMON SCREEN
            "title_simon" to "SIMON DICE",
            "mem_color" to "Memoriza el color...",
            "mem_seq" to "Memoriza la nueva secuencia...",
            "your_turn" to "¡Tu turno!",
            "error_press_rei" to "¡Fallaste! Pulsa Reiniciar.",
            "insS1" to "Tienes que observar atentamente la secuencia \n\n" +
                    " de colores que se muestran en pantalla.\n",
            "insS2" to "Cuando termine la secuencia, es tu turno de ",
            "insS3" to "repetirla\n\n",
            "insS4" to " sumando los colores en el mismo orden.\n",
            "insS5" to "A más dificultad más rápido irá la sequencia. \n" +
                    "\n" +
                    "Concéntrate bien y trata de superar cada ronda. ¡Buena suerte!",
            //SUDOKU SCREEN
            "correct_sudoku" to "✅ Sudoku Correcto",
            "incorrect_sudoku" to "❌ Sudoku Incorrecto",
            "insK1" to "Debes completar el tablero de Sudoku ",
            "insK2" to "rellenando los números faltantes\n\n",
            "insK3" to "Asegúrate de que cada fila, columna y bloque 3x3 contenga ",
            "insK4" to "los números \n",
            "insK5" to "1 al 9",
            "insK6" to " sin repetir\n\n Información de la dificultad:\t\n - Fácil: 54 celdas visibles\n - Medio: 42 celdas visibles\n - Dificil: 30 celdas visibles",
            "insK7" to "¡Buena suerte!",
            //WORD SEARCH SCREEN
            "title_wordSearch" to "SOPA DE LETRAS",
            "words_find" to "Palabras a encontrar:",
            "validate" to "VALIDAR",
            "you_win" to "¡Has ganado!",
            "you_lose" to "¡Has perdido!",
            "congrats" to "¡Felicidades!",
            "times_up" to "Se acabó el tiempo.",
            "replay" to "Volver a jugar",
            "return_home" to "Volver al inicio",
            "insW1" to "Debes buscar las ",
            "insW2" to "palabras",
            "insW3" to " que se te mostraran en una lista.",
            "insW4" to "Cuento menos tiempo tardes ",
            "insW5" to "MEJOR!!",
            "insW7" to "Información de la dificultad:\t\n - Fácil: 5-8 palabras\n - Medio: 10-15 palabras\n - Dificil: 20-25 palabras",
            "insW6" to "Concéntrate, ¡y buena suerte!",



            ),
        "en" to mapOf(
            //GENERAL
            "play" to "PLAY",
            "play_again" to "PLAY AGAIN",
            "restart" to "RESTART",
            "charging" to "CHARGING...",
            "press_play" to "Press Play",
            "you_won" to "You won: ",
            "time" to "Time:",
            "final_time" to "Final time: ",
            "remaining_time" to "Remaining time: ",
            "back" to "⬅ Back",
            "instructions" to "Instructions",
            "easy" to "Easy",
            "medium" to "Medium",
            "hard" to "Hard",
            "your_score" to "Score: ",
            "neurons" to "\uD83C\uDF40 Neuronas",
            //HOME SCREEN
            "hi" to "Hi, ",
            "categories" to "CATEGORIES",
            "memory" to "MEMORY",
            "logic" to "LOGIC",
            "concentration" to "CONCENTRATION",
            "mental_agility" to "MENTAL AGILITY",
            //LOG OUT
            "log_out" to "Log out",
            "you_sure" to "Are you sure you want to log out?",
            "successful" to "Session closed successfully.",
            "yes" to "Yes",
            "cancel" to "Cancel",
            //SETTINGS SCREEN
            "settings_title" to "Settings",
            "preferences" to "Preferences",
            "language" to "Language",
            "account_profile" to "Account & Profile",
            "change_name" to "Change name",
            "change_email" to "Change email",
            "change_password" to "Change password",
            //COUPLES SCREEN
            "title_couples" to "PAIRS GAME",
            "correct_couples" to "Pairs correct:",
            "incorrect_couples" to "Pairs incorrect:",
            "instructions" to "Instructions",
            "start_game" to "Start game",
            "insC1" to "You must select the different ",
            "insC2" to "pais",
            "insC3" to " on the board.",
            "insC4" to "The faster you finish the ",
            "insC5" to "BETTER!!",
            "insC6" to "Focus, and good luck!",
            "insC7" to "Difficulty information:\t\n - Easy: 8 pairs\n - Medium: 12 pairs\n - Hard: 16 pairs",
            //SIMON SCREEN
            "title_simon" to "SIMON SAYS",
            "mem_color" to "Memorize the color...",
            "mem_seq" to "Memorize the new sequence...",
            "your_turn" to "¡Your turn!",
            "error_press_rei" to "¡You failed! Press Restart.",
            "insS1" to "You must carefully watch the sequence \n\n" +
                    "of colors that appears on the screen.\n",
            "insS2" to "When the sequence ends, it's your turn to ",
            "insS3" to "repeat it\n\n",
            "insS4" to "adding the colors in the same order.\n",
            "insS5" to "The more difficult the sequence will be, the faster it will go. \n" +
                    "\n" +
                    "Focus carefully and try to beat each round. Good luck!",
            //SUDOKU SCREEN
            "correct_sudoku" to "✅ Correct Sudoku",
            "incorrect_sudoku" to "❌ Incorrect Sudoku",
            "insK1" to "You must complete the Sudoku board ",
            "insK2" to "by filling in the missing numbers\n\n",
            "insK3" to "Make sure each row, column, and 3x3 block contains ",
            "insK4" to "the numbers \n",
            "insK5" to "1 to 9",
            "insK6" to " without repeating\n\n Difficulty information:\t\n - Easy: 54 visible cells\n - Medium: 42 visible cells\n - Hard: 30 visible cells",
            "insK7" to "Good luck!",
            //WORD SEARCH SCREEN
            "title_wordSearch" to "WORD SEARCH",
            "words_find" to "Words to find:",
            "validate" to "VALIDATE",
            "you_win" to "¡YOU WIN!",
            "you_lose" to "¡YOU LOSE!",
            "congrats" to "¡CONGRATULATIONS!",
            "times_up" to "Time's up.",
            "replay" to "Replay",
            "return_home" to "Return home",
            "insW1" to "You must look fot the ",
            "insW2" to "words",
            "insW3" to " that will shown you in a list.",
            "insW4" to "The faster you finish the ",
            "insW5" to "BETTER!!",
            "insW7" to "Difficulty information:\t\n - Easy: 5-8 words\n - Medium: 10-15 words\n - Hard: 20-25 words",
            "insW6" to "Focus, and good luck!",

        )
    )


    fun t(key: String): String {
        return translations[LanguageState.current.value]?.get(key) ?: key
    }

    fun setLanguage(lang: String) {
        LanguageState.current.value = lang
    }
    fun translateDifficulty(diff: Difficulty): String {
        return when (diff) {
            Difficulty.EASY -> t("easy")
            Difficulty.MEDIUM -> t("medium")
            Difficulty.HARD -> t("hard")
        }
    }

    val currentLanguage get() = LanguageState.current.value
}