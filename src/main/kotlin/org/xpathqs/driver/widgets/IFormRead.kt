package org.xpathqs.driver.widgets

interface IFormRead {
    fun readInt() = 0
    fun readString() = ""
    fun readBool() = false
    fun isReady() = true
    fun isDisabled() = false
}