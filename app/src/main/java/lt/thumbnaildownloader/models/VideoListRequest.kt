package lt.thumbnaildownloader.models

data class VideoListRequest (val part: String, val maxResults: Int, val searchWord: String, val pageToken: String, val key: String) {

}