data class Music(
    val name: String,
    val artist: String,
    val id: Int,
    val data: String,
    val duration: Int,
    val size: Long,
    val mimeType: String,
    val albumId: Long,
    var isCollected: Boolean = true
)