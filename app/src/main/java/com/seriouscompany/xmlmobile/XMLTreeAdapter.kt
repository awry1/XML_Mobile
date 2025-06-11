package com.seriouscompany.xmlmobile

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class XMLTreeAdapter(
    private val visibleNodes: MutableList<VisibleNode>,
    private val onNodeClick: (Int) -> Unit
) : RecyclerView.Adapter<XMLTreeAdapter.NodeViewHolder>() {

    inner class NodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.node_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tree_node, parent, false)
        return NodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val visibleNode = visibleNodes[position]
        val context = holder.itemView.context

        val tagColor = getPrimaryColor(context)
        val valueColor = getValueColor(context)
        val numberingColor = getNumberingColor(context)

        val tagName = visibleNode.node.nodeName
        val nodeValue = visibleNode.node.nodeValue.orEmpty()
        val depth = visibleNode.depth

        val formattedText = buildString {
            append("$depth. $tagName")
            if (nodeValue.isNotBlank()) append("\n$nodeValue")
        }

        val spannable = SpannableString(formattedText)

        val depthEnd = formattedText.indexOf(" ")
        val tagStart = depthEnd + 1
        val tagEnd = tagStart + tagName.length

        spannable.setSpan(
            ForegroundColorSpan(numberingColor),
            0, depthEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(tagColor),
            tagStart, tagEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(valueColor),
            tagEnd, formattedText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.button.text = spannable
        holder.button.setOnClickListener {
            onNodeClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = visibleNodes.size
}