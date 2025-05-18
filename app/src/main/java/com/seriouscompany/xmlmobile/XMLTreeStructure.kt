package com.seriouscompany.xmlmobile

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.util.Stack

class XMLTreeStructure {
    class Node(val nodeName: String) {
        var nodeValue: String? = null
        private val children: MutableList<Node> = mutableListOf()

        fun getChildren(): List<Node> = children
        fun addChild(child: Node) = children.add(child)

        fun toSimpleString(): String {
            val sb = StringBuilder()
            sb.append(nodeName)
            nodeValue?.let { sb.append(": ").append(it) }
            sb.append("\n")
            for (child in children) {
                sb.append(child.toSimpleString())
            }
            return sb.toString()
        }
    }

    fun parseXMLFromFile(file: File?): Node? {
        var rootNode: Node? = null
        try {
            val parser = Xml.newPullParser()
            parser.setInput(StringReader(file?.content))
            val nodeStack = Stack<Node?>()
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val nodeName = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val newNode = Node(nodeName)
                        if (nodeStack.isNotEmpty()) {
                            nodeStack.peek()?.addChild(newNode)
                        } else {
                            rootNode = newNode
                        }
                        nodeStack.push(newNode)
                    }
                    XmlPullParser.END_TAG -> nodeStack.pop()
                    XmlPullParser.TEXT -> {
                        val text = parser.text.trim()
                        if (text.isNotEmpty()) {
                            nodeStack.peek()?.nodeValue = text
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rootNode
    }
}