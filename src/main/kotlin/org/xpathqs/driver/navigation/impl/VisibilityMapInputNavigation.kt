package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.findAnyParentAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.input
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.log.Log
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator
import org.xpathqs.driver.navigation.base.IPageInternalState
import org.xpathqs.driver.widgets.IFormInput

private const val VISIBILITY_MAP_KEY = "VISIBILITY_MAP"

class VisibilityMapInputNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator, model: IBaseModel) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            val elems = (elem.rootParent as? Block)?.allInnerSelectors?.filter {
                it.customPropsMap.containsKey(VISIBILITY_MAP_KEY)
            }
            elems?.forEach { inputSelector ->
                val map = inputSelector.customPropsMap[VISIBILITY_MAP_KEY] as Map<String, Any>
                map.entries.forEach { (k,v) ->

                        if(v is BaseSelector) {
                            if(elem.name.startsWith(v.name) && v.isHidden) {
                                Log.action("Apply VisibilityMapInputNavigation") {
                                    if (inputSelector is IFormInput) {
                                        inputSelector.input(k, model = model)
                                    } else {
                                        inputSelector.input(k, model = model)
                                    }
                                }
                            }
                        } else if(v is Collection<*>) {
                            v as Collection<BaseSelector>
                            v.forEach { sel ->
                                if(elem.name.startsWith(sel.name) && sel.isHidden) {
                                    Log.action("Apply VisibilityMapInputNavigation") {
                                        if (inputSelector is IFormInput) {
                                            inputSelector.input(k)
                                        } else {
                                            inputSelector.input(k)
                                        }
                                    }
                                }
                            }
                        }


                    if(elem.isVisible) {
                        return
                    }
                }
            }
        }
        base.navigate(elem, navigator, model)
    }
}

fun <T: BaseSelector> T.visibilityMap(map: Map<Any?, Any>) : T {
    this.customPropsMap[VISIBILITY_MAP_KEY] = map
    return this
}