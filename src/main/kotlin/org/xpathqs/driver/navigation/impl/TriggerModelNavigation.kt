package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.IModelBlock
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.widgets.IBaseModel
import kotlin.reflect.KMutableProperty

class TriggerModelNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(sel: ISelector, navigator: INavigator) {
        if(sel is BaseSelector) {
            val isValidationError = sel.hasAnnotation(UI.Widgets.ValidationError::class)

            sel.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.let {
                val ann = sel.findAnnotation<UI.Visibility.Dynamic>()
                    ?: sel.findAnyParentAnnotation<UI.Visibility.Dynamic>()

                val m = it()
                var m2 = if(ann?.modelDepends != null) {
                    m.states[ann?.modelDepends]
                } else null

                if(/*m2 == null &&*/ isValidationError) {
                    val ann = sel.findAnyParentAnnotation<UI.Visibility.Dynamic>()
                    m2 = if(ann?.modelDepends != null) {
                        m.states[ann?.modelDepends]
                    } else null
                }

                val model = m2 ?: m

                var prop = model.findPropBySel(sel)
                if(prop == null && isValidationError) {
                    val inputSelector = (sel.base as? Block)?.findWithAnnotation(UI.Widgets.Input::class)
                    if(inputSelector != null) {
                        prop = model.findPropBySel(inputSelector)
                    }
                }
                if(prop != null) {
                    IBaseModel.ignoreInput.get().push(prop)
                    model.fill(prop as KMutableProperty<*>)
                    IBaseModel.ignoreInput.get().pop()

                    if(isValidationError) {
                        (sel.rootParent as? Block)?.findWithAnnotation(UI.Widgets.Submit::class)?.click()
                    }

                    if(sel.isVisible) {
                        return@navigate
                    }
                }
            }
        }

        return base.navigate(sel, navigator)
    }
}