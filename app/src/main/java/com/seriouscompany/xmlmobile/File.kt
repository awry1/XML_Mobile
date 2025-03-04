package com.seriouscompany.xmlmobile

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class File() : Parcelable {
    var name: String? = null
    var uri: Uri? = null
    var content: String? = null
    var treeRoot: XMLTreeStructure.Node? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        uri = parcel.readParcelable(Uri::class.java.classLoader)
        content = parcel.readString()
        treeRoot = parcel.readParcelable(XMLTreeStructure.Node::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(content)
        parcel.writeParcelable(treeRoot, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<File> {
        override fun createFromParcel(parcel: Parcel): File {
            return File(parcel)
        }

        override fun newArray(size: Int): Array<File?> {
            return arrayOfNulls(size)
        }
    }
}