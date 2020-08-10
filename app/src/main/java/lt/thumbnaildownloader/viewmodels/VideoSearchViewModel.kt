package lt.thumbnaildownloader.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.api.VideoRepository
import lt.thumbnaildownloader.models.VideoListRequest
import lt.thumbnaildownloader.models.VideoListResponse
import java.net.URLEncoder


class VideoSearchViewModel(val app: Application) : AndroidViewModel(app) {

    var videoResult = MutableLiveData<VideoListResponse>()
    private val videoRepository = VideoRepository()
    private val key = app.resources.getString(R.string.key)
    var firstSearch: String? = null
    var imageToView: Bitmap? = null
    var pageToken: String? = null
    var lastSearch: String? = null
    var page = 0

    init {
        firstSearch = "giant dad"

        val request =
            VideoListRequest("snippet", 10,  firstSearch!!, "", key)

        viewModelScope.launch(Dispatchers.IO) {

            val videoResponse =  videoRepository.searchForVideos(request)

            withContext(Dispatchers.Main) {
                videoResult.value = videoResponse
            }
        }
    }

    fun searchForVideos(query: String, pageToken: String = "") {

        page = 0

        val encoded = URLEncoder.encode(query, "UTF-8")
        val request =
            VideoListRequest("snippet", 10, encoded, pageToken, key)

        viewModelScope.launch(Dispatchers.IO) {
            val videoResponse = videoRepository.searchForVideos(request)

            withContext(Dispatchers.Main) {
                videoResult.value = videoResponse
            }
        }
    }

    fun searchForMoreVideos() {
        val encoded = URLEncoder.encode(lastSearch, "UTF-8")

        val request =
            VideoListRequest("snippet", 10, encoded, pageToken!!, key)

        viewModelScope.launch(Dispatchers.IO) {
            val videoResponse = videoRepository.searchForVideos(request)

            withContext(Dispatchers.Main) {
                if(videoResponse != null) {
                    page++
                    videoResult.value!!.items.addAll(videoResponse.items)
                    videoResult.value = videoResult.value
                    pageToken = videoResponse.nextPageToken
                }
            }
        }
    }

    fun saveImage(bitmap: Bitmap?, name: String, description: String): String? {

        return MediaStore.Images.Media.insertImage(app.contentResolver, bitmap, name, description)
    }



}