package org.xpathqs.driver.actions

interface IAction {
    val name: String
        get() = this::class.java.simpleName
}