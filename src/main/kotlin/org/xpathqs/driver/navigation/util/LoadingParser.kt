package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.findAnyParentAnnotation
import org.xpathqs.core.selector.base.hasAnyParentAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.WaitForLoadEnum
import org.xpathqs.driver.navigation.base.INavigableDetermination
import org.xpathqs.driver.navigation.base.IPageState
import org.xpathqs.driver.navigation.impl.PageState

class LoadingParser(
    private val block: Block
) {
    fun parse(): Loading {
        var selectors = block.allInnerSelectors
        if(block is IPageState) {
            val state = (block as IPageState).pageState
            selectors = selectors.filter {
                val ann = it.findAnyParentAnnotation<UI.Visibility.State>()
                ann == null || state == ann.value
            }
        }

        val anyColl = ArrayList<BaseSelector>()
        val allColl = ArrayList<BaseSelector>()

        selectors.forEach { sel ->
            val ann = sel.findAnyParentAnnotation<UI.Nav.WaitForLoad>()
                ?: sel.findAnnotation<UI.Nav.WaitForLoad>()
            ann?.let {
                when(it.type) {
                    WaitForLoadEnum.LOAD_ANY -> anyColl.add(sel)
                    WaitForLoadEnum.LOAD_ALL -> allColl.add(sel)
                }
            }
        }

        return if(anyColl.isNotEmpty()) {
             Loading(loadAnySelectors = anyColl)
        } else if(allColl.isNotEmpty()) {
            Loading(loadAllSelectors = allColl)
        } else {
            Loading(
                loadAllSelectors = if(block is IPageState)
                    selectors else (block as INavigableDetermination).determination.exist
            )
        }
    }
}