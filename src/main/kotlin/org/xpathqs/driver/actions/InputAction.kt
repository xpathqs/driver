package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.extensions.isSecret

open class InputAction(
    val text: String,
    val to: BaseSelector,
    val clearBeforeInput: Boolean = true
) : SelectorInteractionAction(to) {

    override fun toString(): String {
        val value = if (to.isSecret() && text.isNotEmpty()) "******" else text
        return String.format(
            Messages.Actions.Input.toString,
            value, to.name
        )
    }
}