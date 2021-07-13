package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Global
import org.xpathqs.log.style.StyleFactory
import org.xpathqs.log.style.StyleFactory.text
import org.xpathqs.log.style.StyledString
import java.time.Duration

open class WaitForSelectorDisappearAction(
    selector: BaseSelector,
    timeout: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
) : WaitForSelectorAction(selector, timeout) {
    constructor(selector: BaseSelector, ms: Long) : this(selector, Duration.ofMillis(ms))

    override val name: String
        get() = "Wait For Selector Disappear"

    override fun toStyledString(): StyledString {
        return StyledString("Wait For $timeout, of: ${selector.name} to Disappear")
    }
}