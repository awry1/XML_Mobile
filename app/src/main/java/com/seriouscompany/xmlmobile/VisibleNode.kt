package com.seriouscompany.xmlmobile

data class VisibleNode(
    val node: XMLTreeStructure.Node,
    val depth: Int,
    var isExpanded: Boolean = false
)