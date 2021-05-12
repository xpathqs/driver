package org.nac.xpathselector.core

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.actions.WaitAction
import org.xpathqs.driver.const.Global
import org.xpathqs.driver.executor.CacheExecutor
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.log.Log.action
import java.time.Duration

open class WaitUtilCache(
    private val executor: CacheExecutor
) {

    fun waitForVisible(
        selector: BaseSelector,
        duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
    ): Boolean {

        return action("Waiting for visible of $selector, for $duration") {
            waitHelper({ selector.isVisible }, duration)
        }
    }

    fun waitForDisappear(
        selector: BaseSelector,
        duration: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
    ): Boolean {
        return action("Waiting for disappear of $selector, for $duration") {
            waitHelper({ selector.isHidden }, duration)
        }
    }

    private fun waitHelper(func: () -> Boolean, duration: Duration): Boolean {
        val t1 = System.currentTimeMillis()
        fun timeoutNotExpired() = !isTimeoutExpired(t1, duration)

        while (func() && timeoutNotExpired()) {
            executor.execute(WaitAction(Global.REFRESH_CACHE_TIMEOUT))
            executor.refreshCache()
        }

        return timeoutNotExpired()
    }

    private fun isTimeoutExpired(startTime: Long, duration: Duration): Boolean {
        return System.currentTimeMillis() - startTime > duration.toMillis()
    }
}