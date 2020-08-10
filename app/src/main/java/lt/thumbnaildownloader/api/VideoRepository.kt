package lt.thumbnaildownloader.api

import android.util.Log
import lt.thumbnaildownloader.models.VideoItem
import lt.thumbnaildownloader.models.VideoListRequest
import lt.thumbnaildownloader.models.VideoListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoRepository {

    private var webClient: YoutubeClient = ClientGenerator.createService(YoutubeClient::class.java)

    suspend fun searchForVideos(request: VideoListRequest): VideoListResponse? {

        val result = webClient.searchForVideos(request.part, request.maxResults,
            request.searchWord, request.pageToken, request.key)

        var videoList: VideoListResponse? = null

        Log.d("test", request.toString())

        try {
            if(result.isSuccessful) {
                videoList = result.body()
                videoList!!.isSuccessful = true
            }
            else {
                videoList = VideoListResponse()
                videoList.isSuccessful = false

                videoList.message = if(result.message().isNotBlank())
                    result.message()
                else
                    result.errorBody()!!.string()
            }
        }
        catch (e: Throwable) {
            e.printStackTrace()
        }

        return videoList
    }

}