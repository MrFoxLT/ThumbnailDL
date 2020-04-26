package lt.thumbnaildownloader.interfaces

interface SearchCallback {

    fun searchForVideo(query: String, pageToken: String = "")

}