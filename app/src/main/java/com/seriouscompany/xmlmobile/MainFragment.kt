package com.seriouscompany.xmlmobile

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.seriouscompany.xmlmobile.databinding.FragmentMainBinding
import java.io.InputStream

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if there's a file URI passed from MainActivity
        arguments?.getString("fileUri")?.let { fileUriString ->
            val uri = Uri.parse(fileUriString)
            readFileContent(uri)
        }
    }

    private fun readFileContent(uri: Uri) {
        try {
            // Open the file and read its content
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader()?.use { it.readText() }
            binding.textviewFirst.text = content
        } catch (e: Exception) {
            Toast.makeText(context, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
