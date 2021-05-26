package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Messages

open class ClickAction(on: BaseSelector) : SelectorInteractionAction(on) {
    override fun toString(): String {
        return String.format(
            Messages.Actions.Click.toString,
            on.name
        )
    }
}