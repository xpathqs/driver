package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.ILoadableDelegate
import org.xpathqs.driver.navigation.base.IModelBlock
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.widgets.IBaseModel
import kotlin.reflect.full.createInstance

class ModelStateSelectorNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem is BaseSelector) {
            val ann = elem.findAnnotation<UI.Visibility.Dynamic>()
            if(ann != null) {
                if (ann.modelState >= 0) {
                    elem.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.let {
                        if(ann.modelState == IBaseModel.DEFAULT) {
                            it().fill(true)
                        } else {
                            it().states[ann.modelState]?.fill(true)
                        }

                        elem.waitForVisible()
                        return@navigate
                    }
                }
            }
        }

        return base.navigate(elem, navigator)
    }
}