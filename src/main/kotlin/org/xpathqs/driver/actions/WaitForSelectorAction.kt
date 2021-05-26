package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.constants.Messages
import java.time.Duration

open class WaitForSelectorAction(
    val selector: BaseSelector,
    timeout: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
) : WaitAction(timeout) {
    constructor(selector: BaseSelector, ms: Long) : this(selector, Duration.ofMillis(ms))

    override val name: String
        get() = Messages.Actions.WaitForSelector.name

    override fun toString(): String {
        return String.format(
            Messages.Actions.WaitForSelector.toString,
            timeout.seconds,
            selector.name
        )
    }
}