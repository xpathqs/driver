package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.const.Global
import java.time.Duration

open class WaitForSelectorAction(
    val selector: BaseSelector,
    timeout: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
) : WaitAction(timeout) {
    constructor(selector: BaseSelector, ms: Long) : this(selector, Duration.ofMillis(ms))

    override val name: String
        get() = "Wait For Selector"

    override fun toString(): String {
        return "Wait For $timeout, of: ${selector.name}"
    }
}