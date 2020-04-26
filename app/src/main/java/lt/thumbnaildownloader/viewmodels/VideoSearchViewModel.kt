package lt.thumbnaildownloader.viewmodels

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.api.VideoRepository
import lt.thumbnaildownloader.models.VideoListRequest
import lt.thumbnaildownloader.models.VideoListResponse
import java.net.URLEncoder


class VideoSearchViewModel(application: Application) : AndroidViewModel(application) {

    var videoResult: MutableLiveData<VideoListResponse>? = null
    val videoRepository = VideoRepository()
    val key = application.resources.getString(R.string.key)
    var firstSearch: String? = null

    init {

        firstSearch = "giant dad"

        val request =
            VideoListRequest("snippet", 5,  firstSearch!!, "", key)
        videoResult = videoRepository.searchForVideos(request)

    }

    fun searchForVideos(query: String, pageToken: String = "") {

        val encoded = URLEncoder.encode(query, "UTF-8")

        val request =
            VideoListRequest("snippet", 5, encoded, pageToken, key)
        videoResult = videoRepository.searchForVideos(request)

    }

    fun saveImage(bitmap: Bitmap?, name: String, description: String, contentResolver: ContentResolver): String? {

        return MediaStore.Images.Media.insertImage(contentResolver, bitmap, name, description)
    }

}