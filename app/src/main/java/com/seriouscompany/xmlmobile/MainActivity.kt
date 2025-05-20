package com.seriouscompany.xmlmobile

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
    private val treeStructure = XMLTreeStructure()
    private val viewModel by viewModels<MainViewModel>()
    private var isFabMenuOpen = false

    private val getFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = File().apply {
                this.uri = it
                this.content = readFileContent(it)
                this.treeRoot = treeStructure.parseXMLFromFile(this)
            }

            viewModel.file = file
            viewModel.wasFileLoaded = true

            if (file.content!!.contains("<?mso-application progid=\"Word.Document\"?>")) {
                showConfirmationDialog("Word")
                return@let
            } else if (file.content!!.contains("<?mso-application progid=\"Excel.Sheet\"?>")) {
                showConfirmationDialog("Excel")
                return@let
            }

            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.MainFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.loadThemePreference(this)

        if (viewModel.isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fabMenu.setOnClickListener {
            toggleFabMenu()
        }

        binding.fabOption1.setOnClickListener {
            getFile.launch("*/*")
            toggleFabMenu()
        }

        binding.fabOption2.setOnClickListener {
            Toast.makeText(this, "Option 2 clicked", Toast.LENGTH_SHORT).show()
            toggleFabMenu()
        }

        binding.fabOption3.setOnClickListener {
            Toast.makeText(this, "Option 3 clicked", Toast.LENGTH_SHORT).show()
            toggleFabMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val switchItem = menu.findItem(R.id.theme_switch)
        val switchView = switchItem.actionView as SwitchCompat
        switchView.text = getString(R.string.theme_switch)

        switchView.isChecked = viewModel.isDarkModeEnabled

        switchView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != viewModel.isDarkModeEnabled) {
                viewModel.isDarkModeEnabled = isChecked
                viewModel.saveThemePreference(this)

                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                recreate()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun readFileContent(uri: Uri): String? {
        return try {
            contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }

    private fun showFabMenu() {
        binding.fabOption1.apply {
            visibility = android.view.View.VISIBLE
            animate().alpha(1f).setDuration(200).start()
        }
        binding.fabOption2.apply {
            visibility = android.view.View.VISIBLE
            animate().alpha(1f).setDuration(200).start()
        }
        binding.fabOption3.apply {
            visibility = android.view.View.VISIBLE
            animate().alpha(1f).setDuration(200).start()
        }
    }

    private fun hideFabMenu() {
        binding.fabOption1.animate().alpha(0f).setDuration(200).withEndAction {
            binding.fabOption1.visibility = android.view.View.GONE
        }.start()
        binding.fabOption2.animate().alpha(0f).setDuration(200).withEndAction {
            binding.fabOption2.visibility = android.view.View.GONE
        }.start()
        binding.fabOption3.animate().alpha(0f).setDuration(200).withEndAction {
            binding.fabOption3.visibility = android.view.View.GONE
        }.start()
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            hideFabMenu()
            binding.fabMenu.animate().rotation(0f).setDuration(200).start()
        } else {
            showFabMenu()
            binding.fabMenu.animate().rotation(45f).setDuration(200).start()
        }
        isFabMenuOpen = !isFabMenuOpen
    }

    private fun showConfirmationDialog(appName: String) {
        //Dialog to show user problem with opening xml file
        val message = SpannableStringBuilder()
        val part1 = "XML file you are trying to read is supposed to be open by "
        val part2 = "Microsoft Office"
        val part3 = " dedicated app: "
        val part4 = appName

        // Append the first part
        message.append(part1)

        // Make the word "continue" bold
        val continueSpan = SpannableString(part2)
        continueSpan.setSpan(StyleSpan(Typeface.BOLD), 0, part2.length, 0)
        message.append(continueSpan)

        // Append the last part
        message.append(part3)

        val continueSpan1 = SpannableString(part4)
        continueSpan1.setSpan(StyleSpan(Typeface.BOLD), 0, part4.length, 0)
        message.append(continueSpan1)

        AlertDialog.Builder(this)
            .setTitle("Content unsupported")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
//                continueWithApp()
            }
            /*.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                exitApp()
            }*/
            .setCancelable(false)
            .show()
    }

    private fun continueWithApp() {
        // Logic to continue using the app
    }

    private fun exitApp() {
        // Logic to exit the app or show a message
        finish()
    }
}