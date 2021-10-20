package org.xpathqs.driver.navigation

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.findParentWithAnnotation
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
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.extensions.makeVisible
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.annotations.NavOrderType
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.ILoadable
import org.xpathqs.driver.navigation.base.INavigable
import org.xpathqs.driver.page.Page
import org.xpathqs.driver.navigation.base.INavigableDetermination
import java.time.Duration

open class NavExecutor(
    origin: IExecutor,
    protected val navigator: Navigator
) : Decorator(origin) {
    init {
        navigator.init(this)
    }

    override val actions: ActionExecMap = ActionExecMap().apply {
        set(MakeVisibleAction(Selector()).name) {
            executeAction(it as MakeVisibleAction)
        }
    }

    override fun beforeAction(action: IAction) {
        if(action is SelectorInteractionAction) {
            if(action.on.isVisible) {
                Log.info("Элемент видим")
                return
            }
            Log.action(Messages.NavExecutor.beforeAction(action)) {
            //    val ann = (action.on.rootParent as? BaseSelector)?.findAnnotation<NavOrder>()

                val curPage = navigator.currentPage
                val sourcePage = action.on.rootParent as? INavigable

                if(sourcePage != null && curPage != sourcePage) {
                    Log.action("Необходима навигация") {
                        val navigations = navigator.findPath(curPage, sourcePage)
                            ?: throw XPathQsException.NoNavigation()

                        navigations.edgeList.forEach {
                            if(it.action != null) {
                                it.action!!()
                                Thread.sleep(500)
                                (origin as? CachedExecutor)?.refreshCache()
                            }
                            (it.to as? ILoadable)?.waitForLoad(Duration.ofSeconds(30))
                            val cp = navigator.currentPage
                            if((it.to is INavigableDetermination && it.to is Page) && it.to != cp) {
                                throw Exception("Wrong page")
                            }
                        }
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
                   /* val form = action.on.findParentWithAnnotation(UI.Widgets.Form::class) as? IBlockSelectorNavigation
                    form?.navigate(action.on)*/

                    action.on.parents.filterIsInstance<IBlockSelectorNavigation>()?.firstOrNull()?.let {
                        it.navigate(action.on)
                    }
                }
            }

        }
    }

    protected fun executeAction(action: MakeVisibleAction) {
    //    beforeAction(action)
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        selector.makeVisible()
        return super.getAttr(selector, attr)
    }

}