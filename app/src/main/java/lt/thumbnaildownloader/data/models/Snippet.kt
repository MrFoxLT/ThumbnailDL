package lt.thumbnaildownloader.data.models

import com.google.gson.annotations.SerializedName

data class Snippet(
        @SerializedName("title") val title: String,
        @SerializedName("channelTitle") val channelTitle: String,
        @SerializedName("thumbnails") val thumbnails: Map<String, Thumbnail>,
        @SerializedName("description") val description: String
)