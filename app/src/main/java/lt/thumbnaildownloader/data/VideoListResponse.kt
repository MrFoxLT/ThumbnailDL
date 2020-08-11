package lt.thumbnaildownloader.data

import com.google.gson.annotations.SerializedName
import lt.thumbnaildownloader.data.models.VideoItem


data class VideoListResponse(
        @SerializedName("nextPageToken") val nextPageToken: String,
        @SerializedName("items") val items: List<VideoItem>,
        var message: String = "",
        var isSuccessful: Boolean = false
)
{
    constructor() : this("", mutableListOf(), "", false)
}