package lt.thumbnaildownloader.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
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
import lt.thumbnaildownloader.interfaces.IVideoItemCallback
import lt.thumbnaildownloader.models.VideoItem


class VideoAdapter(private val items: MutableList<VideoItem?>, private val context: Context, val callback: IVideoItemCallback)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == VideoItem.VISIBLE) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
        }
        else {
            LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(items[position] == null) VideoItem.LOADING
        else VideoItem.VISIBLE
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is ViewHolder && holder.itemViewType == VideoItem.VISIBLE) {

            val item = items[position]

            val imgUrl = "https://img.youtube.com/vi/${item!!.videoId.id}/maxresdefault.jpg"
            val fallbackUrl = "https://img.youtube.com/vi/${item.videoId.id}/hqdefault.jpg"

            holder.tvVideoName.text = items[position]!!.snippet.title
            holder.tvChannelName.text = items[position]!!.snippet.channelTitle

            Glide.with(context).load(imgUrl).error(
                Glide.with(context).load(fallbackUrl)
            ).into(holder.imgThumbnail)

            holder.btnWatch.setOnClickListener {

                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${item.videoId.id}"))
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=${item.videoId.id}")
                )
                try {
                    context.startActivity(appIntent)
                } catch (ex: ActivityNotFoundException) {
                    context.startActivity(webIntent)
                }
            }

            holder.btnSave.setOnClickListener {

                var bitmap: Bitmap?

                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl).into(object: CustomTarget<Bitmap>() {

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                bitmap = resource
                                callback.onClickedSave(bitmap,
                                    item.snippet.title + ".jpg",
                                    item.snippet.description)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) { }
                    })
            }

            holder.imgThumbnail.setOnClickListener {
                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl).into(object: CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            callback.onClickedPreview(resource, item)
                        }
                        override fun onLoadCleared(placeholder: Drawable?) { }
                    })
            }
        }
    }

    fun addItems(items: List<VideoItem?>) {
        val oldSize = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(oldSize, items.size)
    }

    fun removeLastNullItem() {
        if(items.last() == null) {
            val lastIndex = items.lastIndex
            items.removeAt(lastIndex)
            notifyItemRemoved(lastIndex)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgThumbnail: ImageView = view.img_thumbnail
        val btnSave: MaterialButton = view.btn_save
        val btnWatch: MaterialButton = view.btn_watch
        val tvVideoName: TextView = view.tv_video_name
        val tvChannelName: TextView = view.tv_channel_name
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }


}