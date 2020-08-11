package lt.thumbnaildownloader.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_image_preview.view.*
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.ui.viewmodels.VideoSearchViewModel


class ImagePreviewFragment : Fragment() {

    private lateinit var imgPreview: ImageView
    private val viewModel by activityViewModels<VideoSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_preview, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgPreview = view.img_preview
        imgPreview.setOnTouchListener(ImageMatrixTouchHandler(view.context))

        if(viewModel.imageToView != null) {
            Glide.with(requireContext())
                .asBitmap()
                .load(viewModel.imageToView)
                .into(imgPreview)
        }
    }


}
