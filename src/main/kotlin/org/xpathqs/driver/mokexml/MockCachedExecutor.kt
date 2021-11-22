package org.xpathqs.driver.mokexml

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.cache.Cache
import org.xpathqs.driver.cache.ICache
import org.xpathqs.driver.cache.XmlCache
import org.xpathqs.driver.executor.*

open class MockCachedExecutor(
    origin: IExecutor,
    cache: ICache = XmlCache()
) : CachedExecutor(origin, cache) {

    constructor(xml: String, cache: ICache = XmlCache()): this(
        CachedExecutor(
            Executor(MockDriver(xml)),
            cache
        ),
        cache
    )

    override val actions = ActionExecMap()

    init {
        cache.update(driver.pageSource)
    }

    override fun execute(action: IAction) {}

    override fun getActionHandler(action: IAction): ActionExecLambda {
        return {}
    }

    override fun hasActionHandler(action: IAction): Boolean {
        return false
    }

    override fun refreshCache() {}
    override fun checkCache() {}
    override fun invalidateCache() {}
}