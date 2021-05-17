package org.xpathqs.driver.executor

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.actions.WaitForSelectorAction
import org.xpathqs.driver.actions.WaitForSelectorDisappearAction
import org.xpathqs.driver.cache.ICache
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.log.Log
import java.time.Duration

open class CachedExecutor(
    origin: IExecutor,
    private val cache: ICache
) : Decorator(origin) {

    private val actionHandlerCache = ActionExecMap().apply {
        set(WaitForSelectorAction(Selector()).name) {
            executeAction(it as WaitForSelectorAction)
        }
        set(WaitForSelectorDisappearAction(Selector()).name) {
            executeAction(it as WaitForSelectorDisappearAction)
        }
    }

    override fun isPresent(selector: ISelector): Boolean {
        return cache.isPresent(selector.toXpath())
    }

    protected fun refreshCache() {
        Log.action("Trigger Cache refresh") {
            cache.update(driver.pageSource)
        }
    }

    protected open fun executeAction(action: WaitForSelectorAction) {
        waitHelper({ action.selector.isHidden }, action.timeout)
    }

    protected open fun executeAction(action: WaitForSelectorDisappearAction) {
        waitHelper({ action.selector.isVisible }, action.timeout)
    }

    private fun waitHelper(func: () -> Boolean, duration: Duration): Boolean {
        val t1 = System.currentTimeMillis()
        fun timeoutNotExpired() = !isTimeoutExpired(t1, duration)

        while (func() && timeoutNotExpired()) {
            execute(WaitAction(Global.REFRESH_CACHE_TIMEOUT))
            refreshCache()
        }

        return timeoutNotExpired()
    }

    private fun isTimeoutExpired(startTime: Long, duration: Duration): Boolean {
        return System.currentTimeMillis() - startTime > duration.toMillis()
    }

    override fun hasActionHandler(action: IAction): Boolean {
        if (!actionHandlerCache.containsKey(action.name)) {
            return origin.hasActionHandler(action)
        }
        return true
    }

    override fun getActionHandler(action: IAction): ActionExecLambda {
        return actionHandlerCache[action.name]
            ?: origin.getActionHandler(action)
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        return cache.getAttribute(selector.toXpath(), attr)
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        return cache.getAttributes(selector.toXpath(), attr)
    }
}