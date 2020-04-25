package lt.thumbnaildownloader.viewmodels

import android.app.Application
import android.content.ClipDescription
import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.api.VideoRepository
import lt.thumbnaildownloader.models.VideoListRequest
import lt.thumbnaildownloader.models.VideoListResponse
import java.io.OutputStream
import java.net.URLEncoder


class VideoSearchViewModel(application: Application) : AndroidViewModel(application) {

    var videoResult: MutableLiveData<VideoListResponse>? = null
    val videoRepository = VideoRepository()
    val key = application.resources.getString(R.string.key)

    init {

        val request =
            VideoListRequest("snippet", 5, "giant dad", key)
        videoResult = videoRepository.searchForVideos(request) as MutableLiveData<VideoListResponse>

    }

    fun searchForVideos(query: String) {

        val encoded = URLEncoder.encode(query, "UTF-8")

        val request =
            VideoListRequest("snippet", 5, encoded, key)
        videoResult = null
        videoResult = videoRepository.searchForVideos(request)

    }

    fun saveImage(bitmap: Bitmap?, name: String, description: String, contentResolver: ContentResolver): String? {
        var url: String? = null

        url = MediaStore.Images.Media.insertImage(contentResolver, bitmap, name, description)

        return url
    }

}