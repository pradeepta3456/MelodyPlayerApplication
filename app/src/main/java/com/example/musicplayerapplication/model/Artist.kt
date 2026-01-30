data class Artist(
    val id: Int = 0,
    val name: String,
    val imageUrl: String = "", // Firebase Storage URL
    val plays: Int = 0,
    val songCount: Int = 0,
    val albumCount: Int = 0
)
