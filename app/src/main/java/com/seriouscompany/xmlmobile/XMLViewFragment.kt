package com.seriouscompany.xmlmobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val rootNode = viewModel.file?.treeRoot
        if (rootNode != null) {
            visibleNodes.apply {
                clear()
                addAll(buildVisibleNodes(rootNode))
            }
            setupRecyclerView()
        } else {
//            Toast.makeText(context, "Załaduj plik XML", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = XMLTreeAdapter(visibleNodes) { position ->
            toggleChildren(position)
        }
        binding.treeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@XMLViewFragment.adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun toggleChildren(position: Int) {
        val clickedNode = visibleNodes[position].node
        clickedNode.isExpanded = !clickedNode.isExpanded

        visibleNodes.apply {
            clear()
            viewModel.file?.treeRoot?.let { addAll(buildVisibleNodes(it)) }
        }
        adapter.notifyDataSetChanged()
    }

    private fun buildVisibleNodes(node: XMLTreeStructure.Node, depth: Int = 0): List<VisibleNode> {
        val nodes = mutableListOf<VisibleNode>()
        nodes.add(VisibleNode(node, depth, node.isExpanded))
        if (node.isExpanded) {
            node.getChildren().forEach { child ->
                nodes.addAll(buildVisibleNodes(child, depth + 1))
            }
        }
        return nodes
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}