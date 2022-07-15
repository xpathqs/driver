package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.IModelBlock
import org.xpathqs.driver.navigation.base.INavigator

class FormSelectorValidationErrorNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem is BaseSelector) {
            if(elem.hasAnnotation(UI.Widgets.ValidationError::class)) {
                elem.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.let {
                    it().invalidate(elem)
                    return@navigate
                }
            }
        }

        return base.navigate(elem, navigator)
    }
}