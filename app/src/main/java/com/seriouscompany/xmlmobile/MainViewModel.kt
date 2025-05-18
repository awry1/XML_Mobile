package com.seriouscompany.xmlmobile

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var file: File? = null
//    val recentFiles = mutableListOf<File>()
    var wasFileLoaded: Boolean = false
}