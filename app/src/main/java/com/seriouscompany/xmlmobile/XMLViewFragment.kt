package com.seriouscompany.xmlmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seriouscompany.xmlmobile.databinding.FragmentXmlViewBinding

class XMLViewFragment : Fragment() {

    private var _binding: FragmentXmlViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: XMLTreeAdapter
    private val visibleNodes = mutableListOf<VisibleNode>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentXmlViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = viewModel.file

        if (file?.treeRoot != null) {
            visibleNodes.clear()
            visibleNodes.addAll(buildVisibleNodes(file.treeRoot!!))
            setupRecyclerView()
        } else {
            Toast.makeText(context, "Załaduj plik XML", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = XMLTreeAdapter(visibleNodes) { position ->
            toggleChildren(position)
        }
        binding.treeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.treeRecyclerView.adapter = adapter
    }

    private fun toggleChildren(position: Int) {
        val clickedNode = visibleNodes[position].node
        clickedNode.isExpanded = !clickedNode.isExpanded

        visibleNodes.clear()
        viewModel.file?.treeRoot?.let {
            visibleNodes.addAll(buildVisibleNodes(it))
        }
        adapter.notifyDataSetChanged()
    }

    private fun buildVisibleNodes(node: XMLTreeStructure.Node, depth: Int = 0): List<VisibleNode> {
        val result = mutableListOf<VisibleNode>()
        result.add(VisibleNode(node, depth, node.isExpanded))
        if (node.isExpanded) {
            for (child in node.getChildren()) {
                result.addAll(buildVisibleNodes(child, depth + 1))
            }
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}