package lt.thumbnaildownloader.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_video_list.*
import kotlinx.android.synthetic.main.fragment_video_list.view.*
import lt.thumbnaildownloader.MainActivity
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.adapters.VideoAdapter
import lt.thumbnaildownloader.interfaces.IVideoItemCallback
import lt.thumbnaildownloader.interfaces.SearchCallback
import lt.thumbnaildownloader.models.VideoItem
import lt.thumbnaildownloader.viewmodels.VideoSearchViewModel

class VideoListFragment : Fragment(), SearchCallback {

    private lateinit var rvVideos: RecyclerView
    private lateinit var viewModel: VideoSearchViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        navController = findNavController()
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // owner is MainActivity so we can use this ViewModel in PicturePreviewFragment
        viewModel = ViewModelProvider(requireActivity()).get(VideoSearchViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvVideos = view.rv_video_list
        viewModel.lastSearch = viewModel.firstSearch

        // if we're in this fragment, it's guaranteed that this bitmap is not needed
        viewModel.imageToView = null

        viewModel.videoResult.observe(viewLifecycleOwner, Observer {

            if(it.isSuccessful) {
                viewModel.pageToken = it.nextPageToken

                if(viewModel.page == 0) {
                    initRecyclerView(it.items)
                }
                else {
                    if(rvVideos.adapter != null && rvVideos.adapter is VideoAdapter) {
                        val adapter = rvVideos.adapter as VideoAdapter
                        val lastCount = adapter.itemCount
                        addToRecyclerView(it.items.subList(lastCount, it.items.size).toList())
                    }
                }
            }
            else {
                showDialog(it.message)
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val mainActivity = activity as MainActivity
        mainActivity.addSearchCallback(this)
    }

    private fun addToRecyclerView(items: List<VideoItem?>) {

        val adapter = rvVideos.adapter as VideoAdapter
        adapter.removeLastNullItem()
        adapter.addItems(items)
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

            findNavController().navigate(VideoListFragmentDirections
                .actionDestinationVideosToImagePreviewFragment())
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
                    if (items.last() != null) {

                        items.add(null)
                        adapter.notifyItemInserted(items.lastIndex)
                        viewModel.searchForMoreVideos()
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
