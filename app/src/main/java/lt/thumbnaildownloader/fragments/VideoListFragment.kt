package lt.thumbnaildownloader.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_video_list.view.*
import lt.thumbnaildownloader.MainActivity
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.adapters.VideoAdapter
import lt.thumbnaildownloader.interfaces.SearchCallback
import lt.thumbnaildownloader.models.Snippet
import lt.thumbnaildownloader.models.VideoItem
import lt.thumbnaildownloader.models.VideoListResponse
import lt.thumbnaildownloader.viewmodels.VideoSearchViewModel

class VideoListFragment : Fragment(), SearchCallback {

    private lateinit var rvVideos: RecyclerView
    private var viewModel: VideoSearchViewModel? = null
    private lateinit var navController: NavController
    private var pageToken: String? = null
    private var lastSearch: String? = null
    private lateinit var adapter: VideoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        navController = findNavController()
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(VideoSearchViewModel::class.java)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvVideos = view.rv_video_list

        lastSearch = viewModel?.firstSearch

        viewModel?.videoResult?.observe(viewLifecycleOwner, Observer {

            if (it != null) {
                pageToken = it.nextPageToken
                initRecyclerView(it.items)
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val mainActivity = activity as MainActivity

        mainActivity.addSearchCallback(this)

    }

    fun addToRecyclerView(items: MutableList<VideoItem?>) {

        if(adapter.items.last() == null) {
            adapter.items.remove(adapter.items.last())
        }

        adapter.items.addAll(items)
    }

    fun initRecyclerView(items: MutableList<VideoItem?>) {

        val listener = object : VideoAdapter.VideoItemCallback {
            override fun onClickedSave(bitmap: Bitmap?, name: String, description: String) {

                val result =
                    viewModel?.saveImage(bitmap, name, description, activity!!.contentResolver)

                if (result != null) {
                    Snackbar.make(view!!, "Image saved to ${result}!", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(view!!, "Error saving image!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        adapter = VideoAdapter(items, context!!, listener)
        rvVideos.adapter = adapter
        val layoutManager = LinearLayoutManager(context!!)
        rvVideos.layoutManager = layoutManager

        rvVideos.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    if (items.last() != null) {
                        items.add(null)
                        adapter.notifyItemInserted(items.lastIndex)

                        viewModel?.videoResult?.observe(viewLifecycleOwner, Observer {
                            pageToken = it.nextPageToken

                            if (it != null) {
                                addToRecyclerView(it.items)
                            }
                        })

                        viewModel?.searchForVideos(lastSearch!!, pageToken!!)
                    }
                }
            }
        })

        adapter.notifyDataSetChanged()

    }

    override fun searchForVideo(query: String, pageToken: String) {
        lastSearch = query
        viewModel?.searchForVideos(query)

        viewModel?.videoResult?.observe(viewLifecycleOwner, Observer {

            if(it != null) {
                initRecyclerView(it.items)
            }
        })
    }
}
