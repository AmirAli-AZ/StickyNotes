package com.amirali.stickynotes.model

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty

class Note(var id:Int, var date:Long , var color:String , var content:String) {

    val removeProperty:BooleanProperty = SimpleBooleanProperty()

    fun remove(value:Boolean) {
        removeProperty.set(value)
    }

    fun isRemoved():Boolean {
        return removeProperty.get()
    }
}