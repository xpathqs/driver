package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.driver.actions.ClickAction
import org.xpathqs.driver.actions.SwitchTabAction
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.INavigable
import org.xpathqs.driver.navigation.base.model
import org.xpathqs.driver.page.Page
import kotlin.reflect.full.memberFunctions

class NavigationParser(
    private val page: INavigable
) {
    fun parse() {
        page as Block

        page::class.memberFunctions.forEach { m ->
            val methods = m.annotations.filterIsInstance<UI.Nav.PathTo>()
            if(methods.isNotEmpty()) {
                val ann = methods.first()
                if(ann.byInvoke != Block::class) {
                    page.addNavigation(
                        to = ann.byInvoke.objectInstance!! as INavigable
                    ) {
                        m.call(page)
                    }
                }
            }
        }

        val selectors = page.allInnerSelectors.filter {
            it.hasAnnotation(UI.Nav.PathTo::class)
        }
        selectors.forEach {
            val ann: UI.Nav.PathTo = it.findAnnotation()!!
            if(ann.byClick != Block::class) {
                page.addNavigation(
                    to = ann.byClick.objectInstance!! as INavigable
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

        page.findAnnotation<UI.Nav.PathTo>()?.let { it ->
            it.contains.forEach { cls ->
                val obj = cls.objectInstance!! as INavigable
                val weight = if(it.weight != UI.Nav.PathTo.UNDEF ) it.weight else UI.Nav.PathTo.ALREADY_PRESENT_WEIGHT
                page.addNavigation(obj, weight = weight)
            }
            if(it.bySubmit != Block::class) {
                val obj = it.bySubmit.objectInstance!! as INavigable
                val weight = if(it.weight != UI.Nav.PathTo.UNDEF ) it.weight else UI.Nav.PathTo.DEFAULT_WEIGHT
                page.addNavigation(
                    to = obj,
                    weight = weight
                ) {
                    page.model?.submit(page) ?:
                        throw XPathQsException.NoModelForThePage(page as Page)
                }
            }
        }
    }
}