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
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.log.Log
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.widgets.CheckBox
import java.time.Duration

private const val CHECKBOX_LINKED_KEY = "CHECKBOX_LINKED_KEY"
private class LinkWithCheckbox(
    val checkbox: CheckBox,
    val reverted: Boolean
)

class CheckBoxLinkedNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator, model: IBaseModel) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            val linkedCheckbox = elem.customPropsMap[CHECKBOX_LINKED_KEY] ?:
                elem.parents.firstOrNull {
                    it.customPropsMap.containsKey(CHECKBOX_LINKED_KEY)
                }?.customPropsMap?.get(CHECKBOX_LINKED_KEY)

            (linkedCheckbox as? LinkWithCheckbox)?.let {
                Log.action("Trying to apply CheckBoxLinkedNavigation") {
                    if(it.reverted) it.checkbox.uncheck() else it.checkbox.check()
                    elem.waitForVisible(Duration.ofSeconds(2))
                }
                if(elem.isVisible) {
                    return
                }
            }
        }

        base.navigate(elem, navigator, model)
    }
}

fun <T: BaseSelector> T.linkWithCheckbox(checkbox: CheckBox, reverted: Boolean = false) : T {
    customPropsMap[CHECKBOX_LINKED_KEY] = LinkWithCheckbox(checkbox, reverted)
    return this
}