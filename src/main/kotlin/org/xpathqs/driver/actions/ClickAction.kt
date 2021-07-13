package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Messages
import org.xpathqs.log.style.StyleFactory.selectorName
import org.xpathqs.log.style.StyleFactory.text
import org.xpathqs.log.style.StyledString

open class ClickAction(
    on: BaseSelector,
    val moveMouse: Boolean = true
) : SelectorInteractionAction(on) {
    override fun toStyledString() =
        StyledString.fromDefaultFormatString(
            Messages.Actions.Click.toString,
            on.name
        )
}