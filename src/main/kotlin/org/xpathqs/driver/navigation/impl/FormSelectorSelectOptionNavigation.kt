package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.IModelBlock
import kotlin.reflect.full.createInstance

class FormSelectorSelectOptionNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector) {
        if(elem is BaseSelector) {
            if(elem.hasAnnotation(UI.Widgets.OptionItem::class)) {
                if(elem.base is Block) {
                    (elem.base as Block).findWithAnnotation(UI.Widgets.Select::class)?.click()
                }
            }
        }

        return base.navigate(elem)
    }
}