package org.xpathqs.driver.widgets

interface IFormInput {
    fun input(text: String)
    fun clear() = input("")
}