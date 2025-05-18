package com.seriouscompany.xmlmobile

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.seriouscompany.xmlmobile.databinding.FragmentMainBinding
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = viewModel.file

        if (file?.treeRoot != null) {
            val container = binding.root.findViewById<LinearLayout>(R.id.tree_container)
            container.removeAllViews()
            addNodeView(file.treeRoot!!, container)
        } else if (viewModel.wasFileLoaded) {
            Toast.makeText(context, "Błąd: Brak danych XML", Toast.LENGTH_LONG).show()
        }
    }

    private fun addNodeView(node: XMLTreeStructure.Node, parent: ViewGroup, depth: Int = 0) {
        val context = parent.context
        val nodeView = LayoutInflater.from(context).inflate(R.layout.tree_node, parent, false)
        val button = nodeView.findViewById<Button>(R.id.node_button)

        val text = "${depth}. <${node.nodeName}> ${node.nodeValue.orEmpty()}"
        val spannable = SpannableString(text)

        val depthEnd = text.indexOf(" ")
        val tagStart = text.indexOf("<")
        val tagEnd = text.indexOf(">") + 1
        val valueStart = tagEnd + 1

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.numbering)),
            0, depthEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.tag)),
            tagStart, tagEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.value)),
            valueStart, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        button.text = spannable

        val childContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }

        button.setOnClickListener {
            childContainer.visibility = if (childContainer.isVisible) View.GONE else View.VISIBLE
        }

        parent.addView(button)
        parent.addView(childContainer)

        for (child in node.getChildren()) {
            addNodeView(child, childContainer, depth + 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}