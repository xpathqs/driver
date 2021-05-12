package org.xpathqs.driver.executor

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.IDriver
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.actions.WaitForSelectorAction
import org.xpathqs.driver.actions.WaitForSelectorDisappearAction
import org.xpathqs.driver.cache.ICache
import org.xpathqs.driver.const.Global
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.log.Log
import java.time.Duration

abstract class CacheExecutor(
    driver: IDriver,
    private val cache: ICache
) : BaseExecutor(driver) {

    private val actionHandlerCache = ActionExecMap().apply {
        set(WaitForSelectorAction(Selector()).name) {
            executeAction(it as WaitForSelectorAction)
        }
        set(WaitForSelectorDisappearAction(Selector()).name) {
            executeAction(it as WaitForSelectorDisappearAction)
        }
    }

    override fun isPresent(selector: ISelector): Boolean {
        return cache.isVisible(selector)
    }

    fun refreshCache() {
        Log.action("Trigger Cache refresh") {
            cache.setXml(driver.pageSource)
        }
    }

    private fun executeAction(action: WaitForSelectorAction) {
        waitHelper({ action.selector.isVisible }, action.timeout)
    }

    private fun executeAction(action: WaitForSelectorDisappearAction) {
        waitHelper({ action.selector.isHidden }, action.timeout)
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
            return super.hasActionHandler(action)
        }
        return true
    }

    override fun getActionHandler(action: IAction): ActionExecLambda {
        return actionHandlerCache[action.name]
            ?: super.getActionHandler(action)
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        return cache.getAttribute(selector.toXpath(), attr) ?: ""
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        return cache.getAttributes(selector.toXpath(), attr)
    }
}