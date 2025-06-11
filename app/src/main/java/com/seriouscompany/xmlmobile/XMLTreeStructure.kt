package com.seriouscompany.xmlmobile

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.util.Stack

class XMLTreeStructure {

    class Node(val nodeName: String) {
        var nodeValue: String? = null
        private val children = mutableListOf<Node>()
        var isExpanded = false

        fun getChildren(): List<Node> = children
        fun addChild(child: Node) {
            children.add(child)
        }
    }

    fun parseXMLFromFile(file: File?): Node? {
        if (file?.content.isNullOrEmpty()) return null

        var rootNode: Node? = null
        val nodeStack = Stack<Node>()

        return try {
            val parser = Xml.newPullParser().apply {
                setInput(StringReader(file!!.content))
            }

            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name

                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val newNode = Node(tagName)
                        if (nodeStack.isNotEmpty()) {
                            nodeStack.peek().addChild(newNode)
                        } else {
                            rootNode = newNode
                        }
                        nodeStack.push(newNode)
                    }

                    XmlPullParser.TEXT -> {
                        val text = parser.text.trim()
                        if (text.isNotEmpty()) {
                            nodeStack.peek().nodeValue = text
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (nodeStack.isNotEmpty()) nodeStack.pop()
                    }
                }

                eventType = parser.next()
            }

            rootNode
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}