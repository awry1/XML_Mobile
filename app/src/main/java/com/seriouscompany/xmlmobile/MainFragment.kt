package com.seriouscompany.xmlmobile

import android.os.Bundle
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
        button.text = "${"  ".repeat(depth)}<${node.nodeName}> ${node.nodeValue.orEmpty()}"
        button.setTextColor(0xFF4285F4.toInt())

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
