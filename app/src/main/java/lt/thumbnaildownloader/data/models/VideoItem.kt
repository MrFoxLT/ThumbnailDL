package lt.thumbnaildownloader.data.models

import com.google.gson.annotations.SerializedName

data class VideoItem(
        @SerializedName("snippet") val snippet: Snippet,
        @SerializedName("id") val videoId: VideoId)
{
    companion object {
        const val VISIBLE = 1
        const val LOADING = 2
    }

}