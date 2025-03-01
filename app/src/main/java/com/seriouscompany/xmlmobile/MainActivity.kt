package com.seriouscompany.xmlmobile

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.seriouscompany.xmlmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // Convert data to string
                val bundle = Bundle().apply {
                    putString("fileUri", it.toString())  // Put URI as a string
                }
                // Navigate to the fragment and pass the URI (Uniform Resource Identifier)
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.MainFragment, bundle)            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            getContent.launch("*/*")
//            getContent.launch("*/*xml")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        val switchItem = menu.findItem(R.id.theme_switch)
        val switchView = switchItem.actionView as SwitchCompat
        switchView.text = getString(R.string.theme_switch)

        val currentNightMode =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        switchView.isChecked =
            currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES

        switchView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            recreate()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> {
//                Toast.makeText(this, "settings pressed", Toast.LENGTH_SHORT).apply {
//                    setGravity(Gravity.BOTTOM, 0, 0)
//                }.show()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}