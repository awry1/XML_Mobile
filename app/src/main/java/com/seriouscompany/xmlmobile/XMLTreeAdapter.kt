package com.seriouscompany.xmlmobile

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class XMLTreeAdapter(
    private val visibleNodes: MutableList<VisibleNode>,
    private val onNodeClick: (Int) -> Unit
) : RecyclerView.Adapter<XMLTreeAdapter.NodeViewHolder>() {

    inner class NodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.node_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tree_node, parent, false)
        return NodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val visibleNode = visibleNodes[position]
        val node = visibleNode.node
        val depth = visibleNode.depth
        val context = holder.itemView.context

        val tagName = node.nodeName
        val nodeValue = node.nodeValue.orEmpty()
        val nodePart = if (nodeValue.isNotBlank()) "\n$nodeValue" else ""
        val text = "$depth. $tagName$nodePart"
        val spannable = SpannableString(text)

        val depthEnd = text.indexOf(" ")
        val tagStart = depthEnd + 1
        val tagEnd = tagStart + tagName.length
        val valueStart = tagEnd

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.numbering)),
            0,
            depthEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.tag)),
            tagStart,
            tagEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.value)),
            valueStart,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.button.text = spannable

        holder.button.setOnClickListener {
            onNodeClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = visibleNodes.size
}