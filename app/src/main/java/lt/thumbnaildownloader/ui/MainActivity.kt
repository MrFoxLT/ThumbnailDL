package lt.thumbnaildownloader.ui

import android.Manifest
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import lt.thumbnaildownloader.R
import lt.thumbnaildownloader.interfaces.SearchCallback

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var etSearch: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private var menu: Menu? = null

    var searchCallback: SearchCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

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

        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener(destinationChangedListener)

        val drawerNavView = findViewById<NavigationView>(R.id.nav_drawer)
        drawerNavView.setupWithNavController(navController)

        drawerNavView.setNavigationItemSelectedListener(navItemSelectedListener)
        toolbar = findViewById(R.id.toolbar)

        val toggle = ActionBarDrawerToggle (
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private val navItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { menuItem ->

        drawerLayout.closeDrawer(Gravity.LEFT)

        when(menuItem.itemId) {
            R.id.menu_about -> {
                navController.navigate(R.id.aboutFragment)
            }
        }

        true
    }

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->

            val searchMenu = menu?.findItem(R.id.menu_search)

            when(destination.id) {
                R.id.aboutFragment -> {
                    searchMenu?.isVisible = false
                }
                R.id.videoListFragment -> {
                    searchMenu?.isVisible = true
                }
            }
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menu_search -> {
                if(navController.currentDestination?.id == R.id.videoListFragment)
                    etSearch.visibility = View.VISIBLE

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addSearchCallback(searchCallback: SearchCallback) {
        this.searchCallback = searchCallback
    }

}
