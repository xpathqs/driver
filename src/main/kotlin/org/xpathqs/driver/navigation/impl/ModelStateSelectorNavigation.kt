package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.driver.model.IModelStates
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.IModelBlock
import org.xpathqs.driver.navigation.base.INavigator

class ModelStateSelectorNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            val ann = elem.findAnnotation<UI.Visibility.Dynamic>()
            if(ann != null) {
                if (ann.modelState >= 0) {
                    elem.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.let {
                        if(ann.modelState == IBaseModel.DEFAULT) {
                            it().fill(true)
                        } else {
                            val model = it()
                            if(model.view is IModelStates) {
                                model.view.states[ann.modelState]?.fill(true)
                            } else {
                                model.states[ann.modelState]?.fill(true)
                            }
                        }

                        elem.waitForVisible()
                        if(elem.isVisible) {
                            return
                        }
                    }
                }
            }
        }

        return base.navigate(elem, navigator)
    }
}