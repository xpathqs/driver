package org.xpathqs.driver.mokexml

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.actions.IAction
import org.xpathqs.driver.cache.Cache
import org.xpathqs.driver.cache.XmlCache
import org.xpathqs.driver.executor.*

open class MockCachedExecutor(
    xml: String,
    cache: Cache = XmlCache()
) : IExecutor {

    override val actions = ActionExecMap()
    override val driver = MockDriver(xml)

    private val executor = CachedExecutor(
        Executor(driver),
        cache
    )

    init {
        cache.update(driver.pageSource)
    }

    override fun execute(action: IAction) {
    }

    override fun getElementsCount(selector: ISelector): Int {
        return executor.getElementsCount(selector)
    }

    override fun getActionHandler(action: IAction): ActionExecLambda {
        return {}
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        return executor.getAttr(selector, attr)
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        return executor.getAttrs(selector, attr)
    }

    override fun hasActionHandler(action: IAction): Boolean {
        return false
    }

    override fun isPresent(selector: ISelector): Boolean {
        return executor.isPresent(selector)
    }
}