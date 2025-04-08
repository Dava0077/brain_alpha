package brain.alpha.strings

object Strings {

    private val translations = mapOf(
        "es" to mapOf(
            "settings_title" to "Ajustes",
            "back" to "⬅ Volver",
            "preferences" to "Preferencias",
            "language" to "Idioma",
            "account_profile" to "Cuenta y perfil",
            "change_name" to "Cambiar nombre",
            "change_email" to "Cambiar email",
            "change_password" to "Cambiar contraseña"
        ),
        "en" to mapOf(
            "settings_title" to "Settings",
            "back" to "⬅ Back",
            "preferences" to "Preferences",
            "language" to "Language",
            "account_profile" to "Account & Profile",
            "change_name" to "Change name",
            "change_email" to "Change email",
            "change_password" to "Change password"
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