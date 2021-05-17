package org.xpathqs.driver.moke

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.executor.Executor

open class MkExecutor : Executor(MkDriver()) {
    override fun isPresent(selector: ISelector): Boolean {
        return false
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        return ""
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        return emptyList()
    }
}