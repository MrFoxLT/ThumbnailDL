package lt.thumbnaildownloader.ui.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.item_video.view.*
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.data.models.VideoItem
import lt.thumbnaildownloader.interfaces.IVideoItemCallback


class VideoAdapter(private val items: MutableList<VideoItem?>,
                   private val context: Context,
                   val callback: IVideoItemCallback)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VideoItem.VISIBLE) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
        }
        else {
            LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) VideoItem.LOADING
        else VideoItem.VISIBLE
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder && holder.itemViewType == VideoItem.VISIBLE) {

            val item = items[position]

            val imgUrl = "https://img.youtube.com/vi/${item!!.videoId.id}/maxresdefault.jpg"
            val fallbackUrl = "https://img.youtube.com/vi/${item.videoId.id}/hqdefault.jpg"

            holder.tvVideoName.text = items[position]!!.snippet.title
            holder.tvChannelName.text = items[position]!!.snippet.channelTitle

            Glide.with(context).load(imgUrl).error(
                Glide.with(context).load(fallbackUrl)
                    .also { holder.imageUrl = fallbackUrl }
            ).into(holder.imgThumbnail)

            if (holder.imageUrl.isEmpty())
                holder.imageUrl = imgUrl

            holder.btnWatch.setOnClickListener {
                callback.onClickedWatch(item.videoId.id)
            }

            holder.btnSave.setOnClickListener {

                var bitmap: Bitmap?

                Glide.with(context)
                    .asBitmap()
                    .load(holder.imageUrl).into(object : CustomTarget<Bitmap>() {

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            bitmap = resource
                            callback.onClickedSave(
                                bitmap,
                                item.snippet.title + ".jpg",
                                item.snippet.description
                            )
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }

            holder.imgThumbnail.setOnClickListener {
                Glide.with(context)
                    .asBitmap()
                    .load(holder.imageUrl).into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            callback.onClickedPreview(resource, item)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }
    }

    fun addEmptyItem() {
        items.add(null)
        notifyItemInserted(items.size - 1)
    }

    fun isLoading(): Boolean {
        return if (items.isNotEmpty())
            items.last() == null
        else
            false
    }

    fun addItems(newItems: List<VideoItem?>) {
        val oldSize = items.size
        items.removeAt(items.size - 1)
        notifyItemRemoved(items.size - 1)
        items.addAll(newItems)
        notifyItemRangeInserted(oldSize, newItems.size)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgThumbnail: ImageView = view.img_thumbnail
        val btnSave: MaterialButton = view.btn_save
        val btnWatch: MaterialButton = view.btn_watch
        val tvVideoName: TextView = view.tv_video_name
        val tvChannelName: TextView = view.tv_channel_name
        var imageUrl: String = ""
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

}