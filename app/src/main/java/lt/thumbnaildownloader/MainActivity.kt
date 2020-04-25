package lt.thumbnaildownloader

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController

import kotlinx.android.synthetic.main.activity_main.*
import lt.thumbnaildownloader.interfaces.SearchCallback

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var etSearch: EditText
    var searchCallback: SearchCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment)

        ActivityCompat.requestPermissions(this,
            arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

        etSearch = findViewById(R.id.et_search)
        etSearch.visibility = View.GONE

        etSearch.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCallback?.searchForVideo(v?.text.toString())
                    etSearch.visibility = View.GONE
                    v?.text = ""
                    return true
                }

                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menu_settings -> true

            R.id.menu_search -> {

                if(navController.currentDestination?.id == R.id.destinationVideos) {
                    etSearch.visibility = View.VISIBLE
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addSearchCallback(searchCallback: SearchCallback) {
        this.searchCallback = searchCallback
    }


}
