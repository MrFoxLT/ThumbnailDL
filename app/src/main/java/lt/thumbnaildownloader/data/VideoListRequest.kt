package lt.thumbnaildownloader.data

data class VideoListRequest (val part: String, val maxResults: Int, val searchWord: String, val pageToken: String, val key: String) {

    override fun toString(): String {
        return "part=$part maxResults=$maxResults searchWord=$searchWord pageToken=$pageToken key=$key"
    }

}