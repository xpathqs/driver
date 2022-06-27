package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.ILoadableDelegate
import org.xpathqs.driver.navigation.base.IModelBlock
import org.xpathqs.driver.navigation.base.INavigator
import kotlin.reflect.full.createInstance

class ModelStateParentNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem is BaseSelector) {
            elem.findAnyParentAnnotation<UI.Visibility.Dynamic>()?.let { p ->
                if(p.modelState >= 0) {
                    elem.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.let {
                        it().submit(p.modelState)
                        elem.waitForVisible()
                        return@navigate
                    }
                }
            }
        }

        return base.navigate(elem, navigator)
    }
}