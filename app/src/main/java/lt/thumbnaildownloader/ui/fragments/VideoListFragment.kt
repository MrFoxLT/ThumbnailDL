package lt.thumbnaildownloader.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_video_list.*
import kotlinx.android.synthetic.main.fragment_video_list.view.*
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.data.Resource
import lt.thumbnaildownloader.data.models.VideoItem
import lt.thumbnaildownloader.interfaces.IVideoItemCallback
import lt.thumbnaildownloader.interfaces.SearchCallback
import lt.thumbnaildownloader.ui.MainActivity
import lt.thumbnaildownloader.ui.adapters.VideoAdapter
import lt.thumbnaildownloader.ui.viewmodels.VideoSearchViewModel

class VideoListFragment : Fragment(), SearchCallback {

    private lateinit var rvVideos: RecyclerView
    private val viewModel by activityViewModels<VideoSearchViewModel>()
    private lateinit var navController: NavController
    private var isSearchBlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        navController = findNavController()
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvVideos = view.rv_video_list
        viewModel.lastSearch = viewModel.firstSearch

        // if we're in this fragment, it's guaranteed that this bitmap is not needed
        viewModel.imageToView = null

        viewModel.videoListObservable.observe(viewLifecycleOwner, Observer { result ->

            when(result) {
                is Resource.Success -> {
                    if (viewModel.page == 0) {
                        initRecyclerView(result.data.toMutableList())
                    }
                    else {
                        if (rvVideos.adapter == null) {
                            initRecyclerView(result.data.toMutableList())
                        }
                        else if (rvVideos.adapter is VideoAdapter) {
                            val adapter = rvVideos.adapter as VideoAdapter
                            adapter.addItems(result.data.subList(adapter.itemCount - 1, result.data.size - 1))
                            isSearchBlocked = false
                        }
                    }
                }
                is Resource.Failure -> {
                    showDialog(result.message)
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val mainActivity = activity as MainActivity
        mainActivity.addSearchCallback(this)
    }

    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private val videoListener = object : IVideoItemCallback {
        override fun onClickedSave(bitmap: Bitmap?, name: String, description: String) {

            val result =
                viewModel.saveImage(bitmap, name, description)

            if (result != null) {
                Snackbar.make(view!!, "Image saved to ${result}!", Snackbar.LENGTH_SHORT).show()
            }
            else {
                Snackbar.make(view!!, "Error saving image!", Snackbar.LENGTH_SHORT).show()
            }
        }

        override fun onClickedPreview(bitmap: Bitmap?, item: VideoItem) {
            viewModel.imageToView = bitmap

            findNavController().navigate(
                VideoListFragmentDirections
                    .actionVideoListFragmentToImagePreviewFragment()
            )
        }

        override fun onClickedWatch(videoId: String) {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${videoId}"))
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=${videoId}")
            )
            try {
                requireContext().startActivity(appIntent)
            }
            catch (ex: ActivityNotFoundException) {
                requireContext().startActivity(webIntent)
            }
        }
    }

    private fun initRecyclerView(items: MutableList<VideoItem?>) {

        val adapter = VideoAdapter(items, context!!, videoListener)
        rvVideos.adapter = adapter
        rvVideos.adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val layoutManager = LinearLayoutManager(context!!)
        rvVideos.layoutManager = layoutManager

        rvVideos.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    if (!adapter.isLoading())
                        adapter.addEmptyItem()

                    if (!isSearchBlocked) {
                        viewModel.searchForMoreVideos()
                        isSearchBlocked = true
                    }
                }
            }
        })

        adapter.notifyDataSetChanged()
    }

    override fun searchForVideo(query: String, pageToken: String) {
        viewModel.lastSearch = query
        viewModel.searchForVideos(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rv_video_list.adapter = null
    }
}
