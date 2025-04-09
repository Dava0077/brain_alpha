package brain.alpha.strings

object Strings {

    private val translations = mapOf(
        "es" to mapOf(
            //GENERAL
            "play" to "PLAY",
            "reiniciar" to "REINICIAR",
            "press_play" to "Presiona Jugar",
            "you_won" to "Has ganado",
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


            ),
        "en" to mapOf(
            //GENERAL
            "play" to "PLAY",
            "reiniciar" to "RESTART",
            "press_play" to "Press Play",
            "you_won" to "You won",
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