package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.ILoadableDelegate
import org.xpathqs.driver.navigation.base.IModelBlock
import kotlin.reflect.full.createInstance

class ModelStateParentNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector) {
        if(elem is BaseSelector) {
            elem.findAnyParentAnnotation<UI.Visibility.Dynamic>()?.let { p ->
                if(p.modelState >= 0) {
                    val form = elem.findAnyParentAnnotation<UI.Widgets.Form>()
                    if(form != null) {
                        val model = form.model.createInstance()
                        model.submit(p.modelState)
                        elem.waitForVisible()
                        return
                    } else {
                        elem.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.let {
                            it().submit(p.modelState)
                            elem.waitForVisible()
                            return@navigate
                        }
                    }
                }
            }
        }

        return base.navigate(elem)
    }
}