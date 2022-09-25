package org.xpathqs.driver.navigation

import org.jgrapht.GraphPath
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.findParentWithAnnotation
import org.xpathqs.core.selector.base.hasAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.actions.MakeVisibleAction
import org.xpathqs.driver.actions.SelectorInteractionAction
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.executor.ActionExecMap
import org.xpathqs.driver.executor.CachedExecutor
import org.xpathqs.driver.executor.Decorator
import org.xpathqs.driver.executor.IExecutor
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.extensions.makeVisible
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.annotations.NavOrderType
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.*
import org.xpathqs.driver.navigation.impl.Navigable
import org.xpathqs.driver.page.Page
import java.time.Duration

open class NavExecutor(
    origin: IExecutor,
    val navigator: Navigator
) : Decorator(origin) {
    init {
        navigator.init(this)
    }

    fun refreshCache() {
        (origin as? CachedExecutor)?.refreshCache()
    }

    override val actions: ActionExecMap = ActionExecMap().apply {
        set(MakeVisibleAction(Selector()).name) {
            executeAction(it as MakeVisibleAction)
        }
    }

    override fun beforeAction(action: IAction) {
        if(action is SelectorInteractionAction) {
            Log.action(Messages.NavExecutor.beforeAction(action)) {
            //    val ann = (action.on.rootParent as? BaseSelector)?.findAnnotation<NavOrder>()

                var curPage = navigator.currentPage
                val sourcePage = action.on.rootParent as? INavigable

                val blockIsVisible = (sourcePage !is Page
                    && (sourcePage as? INavigableDetermination)?.isVisible == true)

                if(sourcePage != null && curPage != sourcePage && !blockIsVisible && sourcePage is Page) {
                    Log.action("Необходима навигация") {
                        if((curPage as? Block)?.hasAnnotation(UI.Nav.Autoclose::class) == true) {
                            val closeBtn = (curPage as Block).findWithAnnotation(UI.Widgets.Back::class) ?:
                                 (curPage as Block).findWithAnnotation(UI.Widgets.ClickToClose::class)

                            closeBtn?.click()

                            curPage = navigator.currentPage
                        }
                        navigator.navigate(NavWrapper(curPage), NavWrapper.get(sourcePage))
                    }

                    val endPage = navigator.currentPage as Page
                    Log.info("Текущая страница: " + endPage.name)
                }

                if(action.on.isHidden) {
                    action.on.parents.reversed().forEach {
                        (it as? INavigable)?.navigate()
                    }
                }

                if(action.on.isHidden) {
                    var navigations: GraphPath<NavWrapper, Edge>? =
                        if(action.on.base is INavigable) {
                            navigator.findPath(NavWrapper(curPage), NavWrapper.get(action.on.base as INavigable))
                        } else null

                    if(navigations != null) {
                        navigations.edgeList.forEach {
                            if(it.action != null) {
                                it.action!!()
                                Thread.sleep(500)
                                (origin as? CachedExecutor)?.refreshCache()
                            }
                            (it.to.nav as? ILoadable)?.waitForLoad(Duration.ofSeconds(30))
                        }
                    } else {
                        action.on.parents.filterIsInstance<IBlockSelectorNavigation>()?.firstOrNull()?.let {
                            it.navigate(action.on, navigator)
                        }
                    }
                }

                processBeforeActionExtensions(action)
            }
        }
    }

    override fun afterAction(action: IAction) {
        super.afterAction(action)
        processAfterActionExtensions(action)
    }

    protected fun executeAction(action: MakeVisibleAction) {
    //    beforeAction(action)

       /* val sel = action.on
        if(sel.isHidden) {
            val root = sel.rootParent
            if(root is IBlockSelectorNavigation) {
                root.navigate(sel, navigator)
            }
        }*/
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        selector.makeVisible()
        return super.getAttr(selector, attr)
    }

}