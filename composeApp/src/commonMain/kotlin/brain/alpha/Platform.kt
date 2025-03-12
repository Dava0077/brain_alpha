package brain.alpha

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform