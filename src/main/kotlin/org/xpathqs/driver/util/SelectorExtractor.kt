package org.xpathqs.driver.util

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.UI.Visibility.Companion.UNDEF_STATE
import org.xpathqs.driver.navigation.impl.PageState.Companion.isStaticSelector
import org.xpathqs.driver.page.Page
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.jvm.kotlinProperty

interface ISelectorExtractor {
    val staticSelectors : Collection<BaseSelector>
    val dynamicSelectors : Collection<BaseSelector>
}

class SelectorExtractor(
    val source: Block,
    val state: Int = UNDEF_STATE,
    private val stateFilter: IStateFilter = StateFilter()
) : ISelectorExtractor {
    override val staticSelectors by lazy {
        stateFilter.filter(
            selectors.filter {
                isStaticSelector(it) && filter(it)
            },
            state
        )
    }

    override val dynamicSelectors by lazy {
        stateFilter.filter(
            selectors.filter {
                !isStaticSelector(it)
                    && !it.hasAnnotation(UI.Widgets.ValidationError::class)
                    && !it.hasAnyParentAnnotation(UI.Widgets.ValidationError::class)
                    && filter(it)
                },
            state
        )
    }

    
    private val selectors : Collection<BaseSelector>
        get() {
            val result = ArrayList<BaseSelector>()
            if(source is Page) {
                source::class.findAnnotations<UI.Nav.PathTo>().forEach { ann ->
                    if(ann.contain != Block::class) {
                        result.addAll(ann.contain.objectInstance?.allInnerSelectors ?: listOf())
                    }

                }
            } else {
                source.property?.findAnnotations<UI.Nav.PathTo>()?.forEach { ann ->
                    if(ann.contain != Block::class) {
                        result.addAll(ann.contain.objectInstance?.allInnerSelectors ?: listOf())
                    }
                }
            }

            result.addAll(source.allInnerSelectors)
            return result
        }

    private fun filter(it: BaseSelector): Boolean {
        return it.property!!.visibility == KVisibility.PUBLIC
    }
}