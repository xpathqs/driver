package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.block.selectorBlocks
import org.xpathqs.core.selector.block.selectors
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
        val oneOfSelectors = block.getOneOfSelectors()

        if(oneOfSelectors.isNotEmpty()) {
            return Loading(loadAnySelectors = oneOfSelectors)
        }

        selectors.forEach { sel ->
            val ann = sel.findAnyParentAnnotation<UI.Nav.WaitForLoad>()
                ?: sel.findAnnotation<UI.Nav.WaitForLoad>()
            ann?.let {
                when(it.type) {
                    WaitForLoadEnum.LOAD_ANY -> anyColl.add(sel)
                    WaitForLoadEnum.LOAD_ALL -> allColl.add(sel)
                    else -> {}
                }
            }
        }

        return if(anyColl.isNotEmpty()) {
             Loading(loadAnySelectors = anyColl)
        } else if(allColl.isNotEmpty()) {
            Loading(loadAllSelectors = allColl)
        } else {
            //BackendGroup ...
            Loading(
                loadAllSelectors =
                if(block is IPageState) {
                    selectors
                } else {
                    (block as INavigableDetermination).determination.exist.ifEmpty { selectors }
                }
            )
        }
    }
}

fun Block.getRootWithOnOfSelectors(): Block? {
    var res: Block? = this

    while (res?.getOneOfSelectors()?.isEmpty() == true) {
        res = res.base as? Block
    }

    return res
}

fun Block.getOneOfSelectors() : Collection<BaseSelector> {
    val oneOfSelectors = ArrayList<BaseSelector>()

    selectors.forEach {
        if(it.hasAnnotation(UI.Visibility.OneOf::class)) {
            oneOfSelectors.add(it)
        }
    }

    selectorBlocks.forEach {
        if(it.hasAnnotation(UI.Visibility.OneOf::class)) {
            oneOfSelectors.addAll(it.allInnerSelectors)
        }
    }

    if(selectors.isEmpty() && selectorBlocks.size == 1 && oneOfSelectors.isEmpty()) {
        return selectorBlocks.first().getOneOfSelectors()
    }

    return oneOfSelectors
}