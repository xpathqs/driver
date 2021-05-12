package org.xpathqs.fake

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.executor.BaseExecutor

open class FakeBaseExecutor : BaseExecutor(FakeDriver()) {
    override fun isPresent(selector: ISelector): Boolean {
        return false
    }

    override fun getAttr(selector: BaseSelector, attr: String): String {
        
    }

    override fun getAttrs(selector: BaseSelector, attr: String): Collection<String> {
        
    }
}