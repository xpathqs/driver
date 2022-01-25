package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.isChildOf
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.widgets.CheckBox
import java.time.Duration

class CheckBoxNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector) {
        if(elem is BaseSelector) {
            val cb = (elem.rootParent as? Block)?.allInnerSelectors?.firstOrNull {
                it.hasAnnotation(UI.Widgets.Checkbox::class)
            }
            if(cb != null && cb is CheckBox) {
                val ann = cb.findAnnotation<UI.Widgets.Checkbox>()
                if(ann != null) {
                    val checkedBlock = ann.onChecked.objectInstance!!
                    val uncheckedBlock = ann.onUnchecked.objectInstance!!
                    var wasFound = false

                    if(elem.isChildOf(checkedBlock)) {
                        cb.check()
                        wasFound = true
                    } else if(elem.isChildOf(uncheckedBlock)) {
                        cb.uncheck()
                        wasFound = true
                    }

                    if(wasFound) {
                        elem.waitForVisible(Duration.ofSeconds(1))
                        if(elem.isVisible) {
                            return
                        }
                    }
                }
            }
        }

        return base.navigate(elem)
    }
}