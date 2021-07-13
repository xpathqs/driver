package org.xpathqs.driver.actions

import org.xpathqs.log.style.StyledString

interface IAction {
    val name: String
        get() = this::class.java.simpleName

    fun toStyledString(): StyledString
        = StyledString(toString())
}