package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectorBlocks
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.driver.actions.SwitchTabAction
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.UI.Visibility.Companion.UNDEF_STATE
import org.xpathqs.driver.navigation.base.ILoadable
import org.xpathqs.driver.navigation.base.INavigable
import org.xpathqs.driver.navigation.base.model
import org.xpathqs.driver.page.Page
import java.time.Duration
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberFunctions

class NavigationParser(
    private val page: INavigable
) {
    @OptIn(ExperimentalStdlibApi::class)
    fun parse() {
        page as Block

        page::class.memberFunctions.forEach { m ->
            val methods = m.annotations.filterIsInstance<UI.Nav.PathTo>()
            if(methods.isNotEmpty()) {
                val ann = methods.first()
                if(ann.byInvoke != Block::class) {
                    page.addNavigation(
                        to = ann.byInvoke.objectInstance!! as INavigable,
                        state = ann.pageState,
                        selfState = ann.selfPageState,
                    ) {
                        m.call(page)
                    }
                }
            }
        }

        val selectors = page.allInnerSelectors.filter {
            it.hasAnnotation(UI.Nav.PathTo::class)
        } + page.allInnerSelectorBlocks.filter {
            it.hasAnnotation(UI.Nav.PathTo::class)
        }
        selectors.forEach {
            val ann: UI.Nav.PathTo = it.findAnnotation()!!
            if(ann.byClick != Block::class) {

                page.addNavigation(
                    to = ann.byClick.objectInstance!! as INavigable,
                    state = ann.pageState,
                    selfState = ann.selfPageState,
                ) {
                    it.click()
                    if(ann.switchTab) {
                        Global.executor.execute(
                            SwitchTabAction()
                        )
                    }
                }
            }
        }

        page::class.findAnnotations<UI.Nav.PathTo>().forEach { it ->
            try {
                it.contains.forEach { cls ->
                    val obj = cls.objectInstance!! as INavigable
                    val weight = if(it.weight != UI.Nav.PathTo.UNDEF ) it.weight else UI.Nav.PathTo.ALREADY_PRESENT_WEIGHT
                    page.addNavigation(obj, weight = weight, state = it.pageState)
                }
            } catch(e: Error) {}

            if(it.bySubmit != Block::class) {
                val obj = it.bySubmit.objectInstance!! as INavigable
                val weight = if(it.weight != UI.Nav.PathTo.UNDEF ) it.weight else UI.Nav.PathTo.DEFAULT_WEIGHT

                page.addNavigation(
                    to = obj,
                    weight = weight,
                    selfState = it.selfPageState,
                    state = it.pageState
                ) {
                    val model =
                        if(it.pageState != UI.Nav.PathTo.UNDEF) {
                            val state = if(it.modelState != UNDEF_STATE) it.modelState else it.pageState
                            page.model?.states?.get(state)
                        } else {
                            if(it.modelState != UNDEF_STATE) {
                                page.model?.states?.get(it.modelState)
                            } else {
                                page.model
                            }
                        }

                    model?.submit(page) ?:
                        throw XPathQsException.NoModelForThePage(page as Page)

                    obj as ILoadable
                    obj.waitForLoad(Duration.ofSeconds(30))
                }
            }
        }
    }
}