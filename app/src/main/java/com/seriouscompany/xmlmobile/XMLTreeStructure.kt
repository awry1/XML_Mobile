package com.seriouscompany.xmlmobile

import android.os.Parcelable
import android.os.Parcel
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.util.Stack


class XMLTreeStructure {
    // Define a Node class for the tree structure
    class Node(var nodeName: String) : Parcelable {
        var nodeValue: String? = null
        private var children: MutableList<Node> = ArrayList()

        fun addChild(child: Node) {
            children.add(child)
        }

        constructor(parcel: Parcel) : this(parcel.readString()!!) {
            nodeValue = parcel.readString()
            parcel.readList(children, Node::class.java.classLoader)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(nodeName)
            parcel.writeString(nodeValue)
            parcel.writeList(children)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Node> {
            override fun createFromParcel(parcel: Parcel): Node {
                return Node(parcel)
            }

            override fun newArray(size: Int): Array<Node?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun parseXMLFromFile(file: File?): Node? {
        var rootNode: Node? = null
        try {
            val parser = Xml.newPullParser()
            parser.setInput(StringReader(file?.content))

//            val currentNode: Node? = null
            val nodeStack = Stack<Node?>()
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val nodeName = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val newNode = Node(nodeName)
                        if (!nodeStack.isEmpty()) {
                            nodeStack.peek()!!.addChild(newNode) // Add as child to current node
                        } else {
                            rootNode = newNode // First node is the root
                        }
                        nodeStack.push(newNode) // Push new node onto the stack
                    }

                    XmlPullParser.END_TAG -> nodeStack.pop() // Pop the current node when done
                    XmlPullParser.TEXT -> if (nodeStack.peek() != null) {
                        nodeStack.peek()!!.nodeValue = parser.text
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