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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.seriouscompany.xmlmobile.databinding.ActivityMainBinding
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val treeStructure = XMLTreeStructure()

    private val file = File()
    private val recentFiles = mutableListOf<File>()

    private val getFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            file.uri = it
            file.content = readFileContent(it)
            var appName = ""
            if (file.content!!.contains("<?mso-application progid=\"Word.Document\"?>")) {
                appName = "Word"
            } else if (file.content!!.contains("<?mso-application progid=\"Excel.Sheet\"?>")) {
                appName = "Excel"
            }
            if(appName != ""){
                //val test = binding.textView4.setText("ABECADŁO")
                //Toast.makeText(this, "This is a pop-up message!", Toast.LENGTH_SHORT).show()
                showConfirmationDialog(appName)
                return@let
            }
            file.treeRoot = treeStructure.parseXMLFromFile(file)
            recentFiles.add(file)

            // Create a bundle to pass the file to the fragment
            val bundle = Bundle().apply {
                putParcelable("file", file)
            }

            // Navigate to the fragment and pass the file
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.MainFragment, bundle)
        }
    }

    private fun readFileContent(uri: Uri): String? {
        try {
            // Open the file and read its content
            val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader()?.use { it.readText() }
            return content
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
            return null
        }
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
            getFile.launch("*/*")
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
////        return when (item.itemId) {
////            R.id.action_settings -> {
////                Toast.makeText(this, "settings pressed", Toast.LENGTH_SHORT).apply {
////                    setGravity(Gravity.BOTTOM, 0, 0)
////                }.show()
////                true
////            }
////            else -> super.onOptionsItemSelected(item)
////        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun showConfirmationDialog(appName: String) { //Dialog to show user problem with opening xml file
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
            .setTitle("Content unspported")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                continueWithApp()
            }
            /*.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                exitApp()
            }*/
            .setCancelable(false)
            .show()
    }

    private fun continueWithApp() { //Just testing
        // Logic to continue using the app
    }

    private fun exitApp() { //Just testing
        // Logic to exit the app or show a message
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}