package com.seriouscompany.xmlmobile

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.seriouscompany.xmlmobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val treeStructure = XMLTreeStructure()
    private val viewModel by viewModels<MainViewModel>()

    private val getFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = File().apply {
                this.uri = it
                this.content = readFileContent(it)
                this.treeRoot = treeStructure.parseXMLFromFile(this)
            }
            viewModel.file = file
            viewModel.wasFileLoaded = true

            when {
                file.content?.contains("<?mso-application progid=\"Word.Document\"?>") == true -> {
                    showConfirmationDialog("Word")
                    return@let
                }
                file.content?.contains("<?mso-application progid=\"Excel.Sheet\"?>") == true -> {
                    showConfirmationDialog("Excel")
                    return@let
                }
                else -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.MainFragment)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.loadThemePreference(this)

        val nightMode = if (viewModel.isDarkModeEnabled)
            AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightMode)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabDarkMode.setImageResource(viewModel.darkModeIcon)

        binding.fabMenu.setOnClickListener {
            toggleFabMenu()
        }

        binding.fabFileSelect.setOnClickListener {
            toggleFabMenu()
            getFile.launch("*/*")
        }

        binding.fabDarkMode.setOnClickListener {
            toggleFabMenu()
            toggleDarkMode()
        }

        onBackPressedDispatcher.addCallback(this) {
            if (viewModel.wasFileLoaded) {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.MainFragment)
            } else {
                finish()
            }
        }
    }

    private fun toggleDarkMode() {
        viewModel.isDarkModeEnabled = !viewModel.isDarkModeEnabled
        val mode = if (viewModel.isDarkModeEnabled)
            AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        viewModel.saveThemePreference(this)
    }

    private fun readFileContent(uri: Uri): String? = try {
        contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
    } catch (e: Exception) {
        Toast.makeText(this, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
        null
    }

    private fun showFabMenu() {
        binding.fabDarkMode.apply {
            visibility = android.view.View.VISIBLE
            animate().alpha(1f).setDuration(200).start()
        }
        binding.fabFileSelect.apply {
            visibility = android.view.View.VISIBLE
            animate().alpha(1f).setDuration(200).start()
        }
    }

    private fun hideFabMenu() {
        binding.fabDarkMode.animate().alpha(0f).setDuration(200).withEndAction {
            binding.fabDarkMode.visibility = android.view.View.GONE
        }.start()
        binding.fabFileSelect.animate().alpha(0f).setDuration(200).withEndAction {
            binding.fabFileSelect.visibility = android.view.View.GONE
        }.start()
    }

    private fun toggleFabMenu() {
        if (viewModel.isFabMenuOpen) {
            hideFabMenu()
            // binding.fabMenu.animate().rotation(0f).setDuration(200).start()
        } else {
            showFabMenu()
            // binding.fabMenu.animate().rotation(45f).setDuration(200).start()
        }
        viewModel.isFabMenuOpen = !viewModel.isFabMenuOpen
    }

    private fun showConfirmationDialog(appName: String) {
        val message = SpannableStringBuilder().apply {
            append("XML file you are trying to read is supposed to be open by ")
            append(SpannableString("Microsoft Office").apply {
                setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
            })
            append(" dedicated app: ")
            append(SpannableString(appName).apply {
                setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
            })
        }

        AlertDialog.Builder(this)
            .setTitle("Content unsupported")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }
}