package lt.thumbnaildownloader.adapters

import android.R.id
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.item_video.view.*
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.models.VideoItem


class VideoAdapter(val items: List<VideoItem>, val context: Context, val listener: VideoItemCallback): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is ViewHolder){
            val imgUrl = "https://img.youtube.com/vi/${items[position].videoId.id}/maxresdefault.jpg"

//            Glide.with(context).load(items[position].snippet.thumbnails["high"]?.url).into(holder.imgThumbnail)
            holder.tvVideoName.text = items[position].snippet.title
            holder.tvChannelName.text = items[position].snippet.channelTitle
            Glide.with(context).load(imgUrl).into(holder.imgThumbnail)

            holder.btnWatch.setOnClickListener {

                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${items[position].videoId.id}"))
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=${items[position].videoId.id}")
                )
                try {
                    context.startActivity(appIntent)
                } catch (ex: ActivityNotFoundException) {
                    context.startActivity(webIntent)
                }
            }

            holder.btnSave.setOnClickListener {

                var cachedImage = ImageView(context)
                var bitmap: Bitmap? = null
                var target: Target<Bitmap>? = null

                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl).into(object: CustomTarget<Bitmap>() {

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                            CoroutineScope(Dispatchers.IO).launch {
                                bitmap = resource
                                listener.onClickedSave(bitmap,
                                    items[position].snippet.title + ".jpg",
                                    items[position].snippet.description)
//                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })
            }
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgThumbnail = view.img_thumbnail
        val btnSave = view.btn_save
        val btnWatch = view.btn_watch
        val tvVideoName = view.tv_video_name
        val tvChannelName = view.tv_channel_name
    }

    interface VideoItemCallback {

        fun onClickedSave(bitmap: Bitmap?, name: String, description: String)
    }
}