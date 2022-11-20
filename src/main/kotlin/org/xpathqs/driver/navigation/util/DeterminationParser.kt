package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectorBlocks
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.extensions.doesNotChildOf
import org.xpathqs.driver.navigation.annotations.DeterminationType
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.INavigableDetermination
import org.xpathqs.driver.navigation.impl.PageState.Companion.isStaticSelector


class DeterminationParser(
    private val determination: INavigableDetermination
) {
    fun parse(): Determination {
        var exist = ArrayList<BaseSelector>()
        val notExist = ArrayList<BaseSelector>()

        determination as Block
        val selectorsWithBlocks =
            listOf(determination as BaseSelector) + determination.allInnerSelectorBlocks + determination.allInnerSelectors

        selectorsWithBlocks.forEach {
            val selfAnn: UI.Nav.DeterminateBy? = it.findAnnotation()
            if(selfAnn != null) {
                if(!selfAnn.stateDetermination && selfAnn.determination == DeterminationType.EXIST || selfAnn.determination == DeterminationType.EXIST_ALL) {
                    exist.add(it)
                } else if(!selfAnn.stateDetermination && selfAnn.determination == DeterminationType.NOT_EXIST || selfAnn.determination == DeterminationType.NOT_EXIST_ALL){
                    notExist.add(it)
                }
            } else {
                val parentAnn: UI.Nav.DeterminateBy? = it.findAnyParentAnnotation()
                if(parentAnn != null) {
                    if(parentAnn.determination == DeterminationType.EXIST_ALL) {
                        exist.add(it)
                    } else if(parentAnn.determination == DeterminationType.NOT_EXIST_ALL){
                        notExist.add(it)
                    }
                }
            }
        }

        if(exist.isEmpty()) {
            exist.addAll(determination.allInnerSelectors)
        }

        val hidden = exist.distinctBy { it.name }.filter {
            !(if(it is IBlockNavigation) {
                if(it.selfNavigation !is NullBlockNavigation) {
                    it.selfNavigation.defaultVisibility
                } else {
                    true
                }
            } else {
                true
            })
        }

        val e = exist.distinctBy { it.name }.filter {
            (it doesNotChildOf hidden) && isStaticSelector(it)
        }

        val notBlanked = e.filter {
            it !is Block || !it.isBlank
        }

        return Determination(
            exist = notBlanked,
            notExist = notExist.distinctBy { it.name }
        )
    }
}