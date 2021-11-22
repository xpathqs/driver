package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Global
import org.xpathqs.driver.constants.Messages
import org.xpathqs.log.style.StyledString.Companion.fromDefaultFormatString
import java.time.Duration

open class WaitForSelectorCountAction(
    val selector: BaseSelector,
    val expected: Int = -1,
    val moreThen: Int = -1,
    val lessThen: Int = -1,
    timeout: Duration = Global.WAIT_FOR_ELEMENT_TIMEOUT
) : WaitAction(timeout) {
    constructor(selector: BaseSelector, expectedCount: Int, ms: Long)
            : this(selector, expected = expectedCount, timeout = Duration.ofMillis(ms))

    override val name: String
        get() = "WaitForSelectorCountAction"

    override fun toStyledString() =
        fromDefaultFormatString(
            Messages.Actions.WaitForSelector.toString,
            timeout.seconds.toString() + "s.", selector.name
        )

    fun isWaitCompleted(currentCount: Int): Boolean {
        if(expected >= 0) {
            return expected == currentCount
        }
        if(moreThen >= 0) {
            return  currentCount > moreThen
        }
        if(lessThen >= 0) {
            return  currentCount < lessThen
        }
        return false
    }
}