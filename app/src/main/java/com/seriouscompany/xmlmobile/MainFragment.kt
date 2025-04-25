package com.seriouscompany.xmlmobile

import android.graphics.Color
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
import androidx.fragment.app.Fragment
import com.seriouscompany.xmlmobile.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun addNodeView(node: XMLTreeStructure.Node, parent: ViewGroup, depth: Int = 0) {
        val context = parent.context
        val nodeView = LayoutInflater.from(context).inflate(R.layout.tree_node, parent, false)
        val button = nodeView.findViewById<Button>(R.id.node_button)
        
        val text = "${depth}. <${node.nodeName}> ${node.nodeValue.orEmpty()}"
        val spannable = SpannableString(text)
        // Define the ranges for the colored parts
        val depthEnd = text.indexOf(" ")
        val tagStart = text.indexOf("<")
        val tagEnd = text.indexOf(">") + 1
        val valueStart = tagEnd + 1
        // Apply different colors
        spannable.setSpan( //Color for numbering
            ForegroundColorSpan(Color.RED),
            0,
            depthEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan( //Color for tag
            ForegroundColorSpan(Color.BLUE),
            tagStart,
            tagEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(//Color for tag content
            ForegroundColorSpan(Color.DKGRAY),
            valueStart,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        button.text = spannable

        // Kontener na dzieci
        val childContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setPadding(32, 0, 0, 0)
        }

        button.setOnClickListener {
            childContainer.visibility = if (childContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        parent.addView(button)
        parent.addView(childContainer)

        for (child in node.getChildren()) {
            addNodeView(child, childContainer, depth + 1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<File>("file")?.let { file ->
            if (file.content != null) {
                val container = binding.root.findViewById<LinearLayout>(R.id.tree_container)
                container.removeAllViews()
                file.treeRoot?.let { addNodeView(it, container) }
            } else {
                Toast.makeText(context, "Error reading file", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
