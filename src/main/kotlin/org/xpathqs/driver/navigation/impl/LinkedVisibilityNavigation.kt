package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.log.Log
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator

private const val LINKED_VISIBILITY_KEY = "LINKED_VISIBILITY_KEY"

class LinkedVisibilityNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator, model: IBaseModel) {
        if(elem is BaseSelector) {
            if(elem.isVisible) {
                return
            }
            val elems = (elem.rootParent as? Block)?.allInnerSelectors?.filter {
                it.customPropsMap.containsKey(LINKED_VISIBILITY_KEY)
            }
            elems?.forEach { annotatedSelector ->
                val linked = annotatedSelector.customPropsMap[LINKED_VISIBILITY_KEY] as LinkedVisibility<BaseSelector>
                if(linked.sel == elem) {
                    Log.action("Apply LinkedVisibilityNavigation") {
                        linked.makeVisible.invoke(annotatedSelector)
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

class LinkedVisibility<T: BaseSelector>(
    val sel: BaseSelector,
    val checkLambda: (T) -> Boolean,
    val makeVisible: (T) -> Unit,
    val makeHidden: (T) -> Unit
)

fun <T: BaseSelector> T.linkVisibilityOf(
    sel: BaseSelector,
    checkLambda: (T) -> Boolean,
    makeVisible: (T) -> Unit,
    makeHidden: (T) -> Unit
) : T {
    this.customPropsMap[LINKED_VISIBILITY_KEY] = LinkedVisibility<T>(
        sel,
        checkLambda,
        makeVisible,
        makeHidden
    )
    return this
}