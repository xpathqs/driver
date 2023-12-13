package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.isChildOf
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.extensions.waitForVisible
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.log.Log
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.widgets.CheckBox
import java.time.Duration

class CheckBoxNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator, model: IBaseModel) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            (elem.rootParent as? Block)?.allInnerSelectors?.filter {
                it.hasAnnotation(UI.Widgets.Checkbox::class)
            }?.forEach { cb ->
                if (cb != null && cb is CheckBox) {
                    val ann = cb.findAnnotation<UI.Widgets.Checkbox>()
                    if (ann != null) {
                        var wasFound = false
                        if (ann.visibilityOf != BaseSelector::class) {
                            val block = ann.visibilityOf.objectInstance
                            if (block != null && (elem.isChildOf(block) || elem === block)) {
                                Log.action("Apply CheckBoxNavigation") {
                                    cb.check()
                                    wasFound = true
                                }
                            }

                        } else {
                            val checkedBlock = ann.onChecked.objectInstance
                            val uncheckedBlock = ann.onUnchecked.objectInstance

                            if (checkedBlock != null && uncheckedBlock != null) {
                                if (elem.isChildOf(checkedBlock) || elem === checkedBlock) {
                                    Log.action("Apply CheckBoxNavigation") {
                                        cb.check()
                                        wasFound = true
                                    }
                                } else if (elem.isChildOf(uncheckedBlock) || elem === uncheckedBlock) {
                                    Log.action("Apply CheckBoxNavigation") {
                                        cb.uncheck()
                                        wasFound = true
                                    }
                                }
                            }
                        }
                        if (wasFound) {
                            Log.info("Selector was found")
                            elem.waitForVisible(Duration.ofSeconds(1))
                            if(elem.isHidden) {
                                Log.error("Selector is still hidden even after checkbox navigation")
                            }
                        }

                        if (elem.isVisible) {
                            return
                        }
                    }
                }
            }
        }

        base.navigate(elem, navigator, model)
    }
}