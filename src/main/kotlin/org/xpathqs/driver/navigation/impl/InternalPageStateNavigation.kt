package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.findAnyParentAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.navigation.base.IPageInternalState

class InternalPageStateNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            elem.parents.forEach {
                it.findAnnotation<UI.Visibility.Dynamic>()?.let { ann ->
                    if (ann.internalState != UI.Visibility.UNDEF_STATE) {
                        (elem.rootParent as? IPageInternalState)?.let {
                            it.pageInternalState = ann.internalState
                            if(elem.isVisible) {
                                return
                            }
                        }
                    }
                }
            }

        }
        return base.navigate(elem, navigator)
    }
}