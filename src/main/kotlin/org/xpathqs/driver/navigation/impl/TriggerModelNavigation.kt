package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.navigation.annotations.Model
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.ILoadableDelegate
import org.xpathqs.driver.navigation.base.IModelBlock
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance

class TriggerModelNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector) {
        if(elem is BaseSelector) {
            elem.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.let {
                val model = it()
                val prop = model.findPropBySel(elem)
                if(prop != null) {
                    model.fill(prop as KMutableProperty<*>)
                    return@navigate
                }
            }
        }

        return base.navigate(elem)
    }
}