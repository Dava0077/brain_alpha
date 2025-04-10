package brain.alpha.strings

object Strings {

    private val translations = mapOf(
        "es" to mapOf(
            //GENERAL
            "play" to "JUGAR",
            "play_again" to "JUGAR DE NUEVO",
            "restart" to "REINICIAR",
            "press_play" to "Presiona Jugar",
            "you_won" to "Has ganado: ",
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
            "back" to "⬅ Volver",
            "preferences" to "Preferencias",
            "language" to "Idioma",
            "account_profile" to "Cuenta y perfil",
            "change_name" to "Cambiar nombre",
            "change_email" to "Cambiar email",
            "change_password" to "Cambiar contraseña",
            //COUPLES SCREEN
            "title_couples" to "JUEGO DE PAREJAS",
            "time" to "Tiempo:",
            "final_time" to "Tiempo final: ",
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
            //SIMON SCREEN
            "title_simon" to "SIMON DICE",
            "mem_color" to "Memoriza el color...",
            "mem_seq" to "Memoriza la nueva secuencia...",
            "your_turn" to "¡Tu turno!",
            "error_press_rei" to "¡Fallaste! Pulsa Reiniciar.",
            "neurons" to "\uD83C\uDF40 Neuronas",
            "insS1" to "Tienes que observar atentamente la secuencia \n\n" +
                    " de colores que se muestran en pantalla.\n\n",
            "insS2" to "Cuando termine la secuencia, es tu turno de ",
            "insS3" to "repetirla\n\n",
            "insS4" to " tocando los colores en el mismo orden.\n\nConcéntrate bien y trata de superar cada ronda. ¡Buena suerte!",
            //SUDOKU SCREEN
            "correct_sudoku" to "✅ Sudoku Correcto",
            "incorrect_sudoku" to "❌ Sudoku Incorrecto",
            "insK1" to "Debes completar el tablero de Sudoku ",
            "insK2" to "rellenando los números faltantes\n\n",
            "insK3" to "Asegúrate de que cada fila, columna y bloque 3x3 ",
            "insK4" to "contenga los números del ",
            "insK5" to "1 al 9\n\n",
            "insK6" to " sin repetir. ¡Buena suerte!",
            // ODD EMOJI SCREEN
            "title_oddEmoji" to "EMOJI DIFERENTE",
            "game_over" to "¡Juego Terminado!",
            "level_reached" to "Nivel alcanzado",
            "errors" to "Errores cometidos",
            "insOdd" to "Verás una cuadrícula llena de emojis.\nSolo uno es diferente.\n\n¡Encuéntralo lo más rápido posible!\nTienes un límite de errores.\n\n¡Buena suerte!",
            ),
        "en" to mapOf(
            //GENERAL
            "play" to "PLAY",
            "play_again" to "PLAY AGAIN",
            "restart" to "RESTART",
            "press_play" to "Press Play",
            "you_won" to "You won: ",
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
            "back" to "⬅ Back",
            "preferences" to "Preferences",
            "language" to "Language",
            "account_profile" to "Account & Profile",
            "change_name" to "Change name",
            "change_email" to "Change email",
            "change_password" to "Change password",
            //COUPLES SCREEN
            "title_couples" to "COUPLES GAME",
            "time" to "Time:",
            "final_time" to "Final time: ",
            "correct_couples" to "Couples correct:",
            "incorrect_couples" to "Couples incorrect:",
            "instructions" to "Instructions",
            "start_game" to "Start game",
            "insC1" to "You must select the different ",
            "insC2" to "couples",
            "insC3" to " on the board.",
            "insC4" to "The faster you finish the ",
            "insC5" to "BETTER!!",
            "insC6" to "Focus, and good luck!",
            //SIMON SCREEN
            "title_simon" to "SIMON SAYS",
            "mem_color" to "Memorize the color...",
            "mem_seq" to "Memorize the new sequence...",
            "your_turn" to "¡Your turn!",
            "error_press_rei" to "¡You failed! Press Restart.",
            "neurons" to "\uD83C\uDF40 Neurons",
            "insS1" to "You must carefully watch the sequence \n\n" +
                    "of colors that appears on the screen.\n\n",
            "insS2" to "When the sequence ends, it's your turn to ",
            "insS3" to "repeat it\n\n",
            "insS4" to "by tapping the colors in the same order.\n\nFocus carefully and try to beat each round. Good luck!",
            //SUDOKU SCREEN
            "correct_sudoku" to "✅ Correct Sudoku",
            "incorrect_sudoku" to "❌ Incorrect Sudoku",
            "insK1" to "You must complete the Sudoku board ",
            "insK2" to "by filling in the missing numbers\n\n",
            "insK3" to "Make sure each row, column, and 3x3 block ",
            "insK4" to "contains the numbers",
            "insK5" to "1 to 9\n\n",
            "insK6" to " without repeating.Good luck!",
            // ODD EMOJI SCREEN
            "title_oddEmoji" to "ODD EMOJI",
            "game_over" to "Game Over!",
            "level_reached" to "Level reached",
            "errors" to "Errors made",
            "insOdd" to "You will see a grid of emojis.\nOnly one is different.\n\nFind it as quickly as possible!\nYou have a limited number of mistakes.\n\nGood luck!",

        )
    )


    fun t(key: String): String {
        return translations[LanguageState.current.value]?.get(key) ?: key
    }

    fun setLanguage(lang: String) {
        LanguageState.current.value = lang
    }

    val currentLanguage get() = LanguageState.current.value
}