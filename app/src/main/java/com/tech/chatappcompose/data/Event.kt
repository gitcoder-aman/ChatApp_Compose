package com.tech.chatappcompose.data

open class Event <out T>(private val content : T) {
    private var hasBeenHandled = false
    fun getContentOrNull() : T?{
        return if(hasBeenHandled) null
        else{
            hasBeenHandled = true
            content
        }
    }
}