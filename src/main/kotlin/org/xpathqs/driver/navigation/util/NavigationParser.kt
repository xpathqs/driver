package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.findAnnotations
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectorBlocks
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.driver.actions.SwitchTabAction
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.executor.CachedExecutor
import org.xpathqs.driver.executor.Decorator
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.makeVisible
import org.xpathqs.driver.extensions.ms
import org.xpathqs.driver.extensions.wait
import org.xpathqs.driver.navigation.NavExecutor
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.UI.Visibility.Companion.UNDEF_STATE
import org.xpathqs.driver.navigation.base.ILoadable
import org.xpathqs.driver.navigation.base.INavigable
import org.xpathqs.driver.navigation.base.IPageState
import org.xpathqs.driver.navigation.base.model
import org.xpathqs.driver.page.Page
import java.time.Duration
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod

class NavigationParser(
    private val page: INavigable
) {
    
    fun parse() {
        page as Block

        val methods = (page.allInnerSelectorBlocks + page).flatMap {
            it::class.memberFunctions
        }.filter {
            it.annotations.filterIsInstance<UI.Nav.PathTo>().isNotEmpty()
        }

        if(methods.isNotEmpty()) {
            methods.forEach {
                it.annotations.filterIsInstance<UI.Nav.PathTo>().forEach { ann ->
                    if(ann.byInvoke != Block::class) {
                        page.addNavigation(
                            to = ann.byInvoke.objectInstance!! as INavigable,
                            state = ann.pageState,
                            selfState = ann.selfPageState,
                            globalState = ann.globalState
                        ) {
                            it.call(it.javaMethod!!.declaringClass.kotlin.objectInstance)
                            if (ann.switchTab) {
                                Global.executor.execute(
                                    SwitchTabAction()
                                )
                            }
                        }
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
            val pathToAnnotations = it.property?.findAnnotations<UI.Nav.PathTo>()
                ?: it.findAnnotations()
            pathToAnnotations?.forEach { ann ->
           // it.annotations.forEach { ann ->
               // if(ann is UI.Nav.PathTo) {
                    if (ann.byClick != Block::class) {
                        val weight = if (ann.weight != UI.Nav.PathTo.UNDEF) ann.weight else UI.Nav.PathTo.DEFAULT_WEIGHT

                        page.addNavigation(
                            to = ann.byClick.objectInstance!! as INavigable,
                            weight = weight,
                            state = ann.pageState,
                            selfState = ann.selfPageState,
                            globalState = ann.globalState
                        ) {
                            if (ann.globalState != UNDEF_STATE) {
                                ((Global.executor as Decorator).origin as NavExecutor).globalState.globalState = ann.globalState
                                it.makeVisible()
                            }
                            it.click()
                            if (ann.switchTab) {
                                Global.executor.execute(
                                    SwitchTabAction()
                                )
                            }
                        }
                    //}
                }
            }
        }

        page::class.findAnnotations<UI.Nav.PathTo>().forEach { pathTo ->
            //Add navigation for the "Contains" blocks, which are already present on the page
            //Executor should do nothing, this edge is for JGraph only
            try {
               // it.contains.forEach { cls ->
                if(pathTo.contain != Block::class) {
                    val cls = pathTo.contain
                    val obj = cls.objectInstance!! as INavigable
                    val weight = if(pathTo.weight != UI.Nav.PathTo.UNDEF) pathTo.weight else UI.Nav.PathTo.ALREADY_PRESENT_WEIGHT
                    //val state = (page.findAnnotation<UI.Nav.Config>())?.defaultState ?: UNDEF_STATE

                    page.addNavigation(
                        obj,
                        weight = weight,
                        state = pathTo.pageState,
                        selfState = pathTo.pageState,
                        globalState = pathTo.globalState
                    )

                }
            } catch(e: Error) {}

            if(pathTo.bySubmit != Block::class) {
                val obj = pathTo.bySubmit.objectInstance!! as INavigable
                val weight = if(pathTo.weight != UI.Nav.PathTo.UNDEF ) pathTo.weight else UI.Nav.PathTo.DEFAULT_WEIGHT

                page.addNavigation(
                    to = obj,
                    weight = weight,
                    selfState = pathTo.selfPageState,
                    state = pathTo.pageState,
                    globalState = pathTo.globalState
                ) {
                    val model =
                        if(pathTo.pageState != UI.Nav.PathTo.UNDEF) {
                            val state = if(pathTo.modelState != UNDEF_STATE) pathTo.modelState else pathTo.pageState
                            page.model?.states?.get(state)
                        } else {
                            if(pathTo.modelState != UNDEF_STATE) {
                                page.model?.states?.get(pathTo.modelState)
                            } else {
                                page.model
                            }
                        }

                    model?.submit(page) ?:
                        throw XPathQsException.NoModelForThePage(page as Page)

                    if (pathTo.switchTab) {
                        Global.executor.execute(
                            SwitchTabAction()
                        )
                    }

                    obj as ILoadable
                    obj.waitForLoad(Duration.ofSeconds(30))

                    if(obj is IPageState && pathTo.pageState != UI.Nav.PathTo.UNDEF) {

                        var iterations = 0
                        while (obj.pageState != pathTo.pageState && iterations < 5) {
                            wait(500.ms, "delay in LoadingParser for states")
                            (Global.executor as Decorator).findOriginInstance<CachedExecutor>()?.refreshCache() ?: break
                            iterations++
                        }

                        if(iterations == 5) {
                            throw XPathQsException.IncorrectPageState(page as Page, pathTo.pageState)
                        }

                    }
                }
            }
        }
    }
}