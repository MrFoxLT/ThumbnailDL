package lt.thumbnaildownloader.models

import com.google.gson.annotations.SerializedName

data class VideoId(@SerializedName("videoId") val id: String)

data class PageInfo(
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("resultsPerPage") val resultsPerPage: Int
)

data class Thumbnail(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

data class Snippet(
    @SerializedName("title") val title: String,
    @SerializedName("channelTitle") val channelTitle: String,
    @SerializedName("thumbnails") val thumbnails: Map<String, Thumbnail>,
    @SerializedName("description") val description: String
)

data class VideoItem(
    @SerializedName("snippet") val snippet: Snippet,
    @SerializedName("id") val videoId: VideoId)

data class VideoListResponse(
    @SerializedName("nextPageToken") val nextPageToken: String,
    @SerializedName("items") val items: List<VideoItem>
)