package lt.thumbnaildownloader.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lt.thumbnaildownloader.models.VideoListRequest
import lt.thumbnaildownloader.models.VideoListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class VideoRepository {

    private var webClient: YoutubeClient? = null

    init {
        webClient = ClientGenerator.createService(YoutubeClient::class.java)
    }

    fun searchForVideos(request: VideoListRequest): MutableLiveData<VideoListResponse> {

        val result = MutableLiveData<VideoListResponse>()
        val call = webClient?.searchForVideos(request.part, request.maxResults, request.searchWord, request.key)

        call?.enqueue(object: Callback<VideoListResponse> {

            override fun onResponse(call: Call<VideoListResponse>, response: Response<VideoListResponse>) {

                result.postValue(response.body())
            }

            override fun onFailure(call: Call<VideoListResponse>, t: Throwable) {
                t.printStackTrace()
                if (t is IOException) {

                }
                else {

                }
            }
        })

        return result
    }

}