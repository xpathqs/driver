package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.extensions.makeVisible
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.NavExecutor
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator

class ClickToBackNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            val cp = navigator.currentPage
            if(cp !== elem.rootParent) {
                (cp as? Block)?.allInnerSelectors?.firstOrNull {
                    it.hasAnnotation(UI.Widgets.Back::class)
                }?.let {
                    Log.action("The 'Click to Back' selector was found") {
                        if(it.isVisible) {
                            it.click()

                            Log.action("Trying to make $elem visible, after closing") {
                                elem.makeVisible()
                            }

                            if(elem.isVisible) {
                                Log.info("$elem became visible")
                                return@action
                            }
                        }
                    }
                }
            }
        }

        return base.navigate(elem, navigator)
    }
}