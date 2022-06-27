package org.xpathqs.driver.widgets

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.extensions.parentCount
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.cls
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.navigation.util.IBlockNavigation

open class CheckBox(
    base: BaseSelector,
    private val input: BaseSelector,
    private val label: BaseSelector,
    @UI.Visibility.Dynamic
    private val checkActiveSelector: BaseSelector,
): Block(base), IFormRead, IBlockSelectorNavigation {

    open val isChecked: Boolean
        get() = checkActiveSelector.isVisible

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

    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem == checkActiveSelector) {
            check()
        }
    }


}