package lt.thumbnaildownloader.ui.viewmodels

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
import lt.thumbnaildownloader.data.*
import lt.thumbnaildownloader.data.models.VideoItem
import java.net.URLEncoder


class VideoSearchViewModel(val app: Application) : AndroidViewModel(app) {

    // Resource type for error handling
    var videoListObservable = MutableLiveData<Resource<MutableList<VideoItem>>>()

    private val videoRepository: IDataSource = DataSourceImpl()

    private val key = app.resources.getString(R.string.key)
    var firstSearch: String? = null
    var imageToView: Bitmap? = null
    private var pageToken: String? = null
    var lastSearch: String? = null
    var page = 0

    private val MAX_RESULTS = 3

    init {
        firstSearch = "giant dad"
       searchForVideos(firstSearch!!)
    }

    fun searchForVideos(query: String, pageToken: String = "") {

        page = 0

        val encoded = URLEncoder.encode(query, "UTF-8")
        val request =
            VideoListRequest(
                "snippet",
                MAX_RESULTS,
                encoded,
                pageToken,
                key
            )

        viewModelScope.launch(Dispatchers.IO) {
            val videoResponse = videoRepository.searchForVideos(request)

            when(videoResponse) {
                is Resource.Success -> {
                    val videoItems = videoResponse.data.items

                    withContext(Dispatchers.Main) {
                        this@VideoSearchViewModel.pageToken = videoResponse.data.nextPageToken
                        videoListObservable.value = Resource.Success(videoItems.toMutableList())
                    }
                }
                is Resource.Failure -> {
                    withContext(Dispatchers.Main) {
                        videoListObservable.value = Resource.Failure(videoResponse.message)
                    }
                }
            }
        }
    }

    fun searchForMoreVideos() {
        val encoded = URLEncoder.encode(lastSearch, "UTF-8")

        val request =
            VideoListRequest(
                "snippet",
                MAX_RESULTS,
                encoded,
                pageToken!!,
                key
            )

        viewModelScope.launch(Dispatchers.IO) {
            val videoResponse = videoRepository.searchForVideos(request)

            withContext(Dispatchers.Main) {
                page++

                if(videoResponse is Resource.Success) {
                    if(videoListObservable.value is Resource.Success<MutableList<VideoItem>>) {
                        (videoListObservable.value as Resource.Success).data.addAll(videoResponse.data.items)
                        videoListObservable.value = videoListObservable.value
                    }
                    else {
                        videoListObservable.value = Resource.Success(videoResponse.data.items.toMutableList())
                    }

                    pageToken = videoResponse.data.nextPageToken
                }
            }
        }
    }

    fun saveImage(bitmap: Bitmap?, name: String, description: String): String? {

        return MediaStore.Images.Media.insertImage(app.contentResolver, bitmap, name, description)
    }



}