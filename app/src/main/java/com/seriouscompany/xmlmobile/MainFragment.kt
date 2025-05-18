package com.seriouscompany.xmlmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seriouscompany.xmlmobile.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: XMLTreeAdapter
    private val visibleNodes = mutableListOf<VisibleNode>()

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
            visibleNodes.clear()
            visibleNodes.add(VisibleNode(file.treeRoot!!, 0))
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
        val node = visibleNodes[position]
        val children = node.node.getChildren()

        if (children.isEmpty()) return

        val insertPosition = position + 1

        // Are children already expanded?
        val isExpanded = (insertPosition < visibleNodes.size &&
                visibleNodes[insertPosition].depth > node.depth)

        if (isExpanded) {
            // Delete children (recursively)
            var removeCount = 0
            var i = insertPosition
            while (i < visibleNodes.size && visibleNodes[i].depth > node.depth) {
                removeCount++
                i++
            }
            visibleNodes.subList(insertPosition, insertPosition + removeCount).clear()
            adapter.notifyItemRangeRemoved(insertPosition, removeCount)
        } else {
            // Append children at the right position
            val newNodes = children.map { VisibleNode(it, node.depth + 1) }
            visibleNodes.addAll(insertPosition, newNodes)
            adapter.notifyItemRangeInserted(insertPosition, newNodes.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}