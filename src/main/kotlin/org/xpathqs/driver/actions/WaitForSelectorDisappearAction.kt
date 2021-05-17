package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Global
import java.time.Duration

open class WaitForSelectorDisappearAction(
    selector: BaseSelector,
    timeout: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
) : WaitForSelectorAction(selector, timeout) {
    constructor(selector: BaseSelector, ms: Long) : this(selector, Duration.ofMillis(ms))

    override val name: String
        get() = "Wait For Selector Disappear"

    override fun toString(): String {
        return "Wait For $timeout, of: ${selector.name} to Disappear"
    }
}