package org.xpathqs.driver.actions

import org.xpathqs.log.style.StyledString
import java.time.Duration

open class WaitAction(
    val timeout: Duration = Duration.ZERO
) : IAction {
    override val name: String
        get() = "Wait"

    override fun toString(): String {
        return "Wait for $timeout"
    }
}