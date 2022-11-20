package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.isChildOf
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.widgets.CheckBox
import java.time.Duration

private const val CHECKBOX_LINKED_KEY = "CHECKBOX_LINKED_KEY"

class CheckBoxLinkedNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            val linkedCheckbox = elem.customPropsMap[CHECKBOX_LINKED_KEY] ?:
                elem.parents.firstOrNull {
                    it.customPropsMap.containsKey(CHECKBOX_LINKED_KEY)
                }?.customPropsMap?.get(CHECKBOX_LINKED_KEY)

            (linkedCheckbox as? CheckBox)?.let {
                it.check()
                if(elem.isVisible) {
                    return
                }
            }
        }

        return base.navigate(elem, navigator)
    }
}

fun <T: BaseSelector> T.linkWithCheckbox(checkbox: CheckBox) : T {
    customPropsMap[CHECKBOX_LINKED_KEY] = checkbox
    return this
}