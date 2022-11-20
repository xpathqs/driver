package org.xpathqs.driver.executor

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.driver.actions.*
import org.xpathqs.driver.cache.ICache
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.extensions.count
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.log.Log
import java.time.Duration

open class CachedExecutor(
    origin: IExecutor,
    val cache: ICache
) : Decorator(origin) {

    private var needRefreshCache = true

    private val actionHandlerCache = ActionExecMap().apply {
        set(WaitForSelectorAction(Selector()).name) {
            executeAction(it as WaitForSelectorAction)
        }
        set(WaitForFirstSelectorAction(listOf(Selector())).name) {
            executeAction(it as WaitForFirstSelectorAction)
        }
        set(WaitForAllSelectorAction(listOf(Selector())).name) {
            executeAction(it as WaitForAllSelectorAction)
        }
        set(WaitForSelectorDisappearAction(Selector()).name) {
            executeAction(it as WaitForSelectorDisappearAction)
        }
        set(WaitForSelectorCountAction(Selector(), 0).name) {
            executeAction(it as WaitForSelectorCountAction)
        }
    }

    override fun isPresent(selector: ISelector): Boolean {
        checkCache()
        return cache.isPresent(selector.toXpath())
    }

    open fun refreshCache() {
        Log.action("Trigger Cache refresh") {
            cache.update(driver.pageSource)
            needRefreshCache = false
        }
    }

    protected open fun executeAction(action: WaitForSelectorAction) {
        waitHelper({ action.selector.isHidden }, action.timeout)
    }

    protected open fun executeAction(action: WaitForFirstSelectorAction) {
        Log.action("WaitForFirstSelectorAction") {
            waitHelper(
                {
                    action.selectors.firstOrNull {
                        Log.info("${it.name} isVisible: ${it.isVisible}")
                        it.isVisible
                    } == null
                },
                action.timeout
            )
        }
    }

    protected open fun executeAction(action: WaitForAllSelectorAction) {
        Log.action("WaitForAllSelectorAction") {
            waitHelper(
                {
                    action.selectors.firstOrNull {
                        Log.info("${it.name} isVisible: ${it.isVisible}")
                        it.isHidden
                    } != null
                },
                action.timeout
            )
        }
    }

    protected open fun executeAction(action: WaitForSelectorDisappearAction) {
        waitHelper({ action.selector.isVisible }, action.timeout)
    }

    protected open fun executeAction(action: WaitForSelectorCountAction) {
        waitHelper({
            val c = action.selector.count
            Log.trace("${action.selector.name} count: $c")
            !action.isWaitCompleted(c)
        }, action.timeout)
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

    override fun getElementsCount(selector: ISelector): Int {
        return cache.getElementsCount(selector.toXpath())
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        return Log.action("Get '$attr' of '${selector}'") {
            Log.xpath(selector)
            checkCache()
            cache.getAttribute(selector.toXpath(), attr)
        }
    }

    override fun getAllAttrs(selector: BaseSelector): Collection<Pair<String, String>> {
        return Log.action("Get all attributes of '${selector}'") {
            Log.xpath(selector)
            checkCache()
            cache.getAttributes(selector.toXpath())
        }
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        return Log.action("Get all '$attr' of '${selector}'") {
            Log.xpath(selector)
            checkCache()
            cache.getAttributes(selector.toXpath(), attr)
        }
    }

    protected open fun invalidateCache() {
        Log.trace("Cache marked as invalidated")
        needRefreshCache = true
    }

    protected open fun checkCache() {
        if(needRefreshCache) {
            Log.trace("Cache is invalid, updating...")
            refreshCache()
        }
    }

    override fun afterAction(action: IAction) {
        super.afterAction(action)
        if(action is SelectorInteractionAction && action !is MakeVisibleAction) {
            invalidateCache()
        } else {
            needRefreshCache = false
        }
    }
}