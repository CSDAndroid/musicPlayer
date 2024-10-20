data class Song(
    val name: String,
    val artist: String,
    val id: Int,
    val duration: Int,
    val size: Long,
    val mimeType: String,
    val data: String,
    val albumId: Long,
    var isCollected: Boolean = false
)