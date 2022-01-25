package org.xpathqs.driver.widgets

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.extensions.parentCount
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.cls

open class CheckBox(
    base: BaseSelector,
    private val input: BaseSelector,
    private val label: BaseSelector
): Block(base), IFormRead {

    open val isChecked: Boolean
        get() = input.parentCount(2).cls.contains("Checkbox__active")

    fun check() {
        if(!isChecked) {
            click()
        }
    }

    fun uncheck() {
        if(isChecked) {
            click()
        }
    }

    override fun readBool(): Boolean {
        return isChecked
    }
}