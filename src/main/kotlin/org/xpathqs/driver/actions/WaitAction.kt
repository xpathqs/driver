package org.xpathqs.driver.actions

import java.time.Duration

open class WaitAction(
    val timeout: Duration = Duration.ZERO,
    val logMessage: String = ""
) : IAction {
    override val name: String
        get() = "Wait"

    override fun toString(): String {
        return "Wait for $timeout" + if(logMessage.isNotEmpty()) ". $logMessage" else ""
    }
}