package lt.thumbnaildownloader.interfaces

import android.graphics.Bitmap
import lt.thumbnaildownloader.models.VideoItem

interface IVideoItemCallback {

    fun onClickedSave(bitmap: Bitmap?, name: String, description: String)
    fun onClickedPreview(bitmap: Bitmap?, item: VideoItem)
}