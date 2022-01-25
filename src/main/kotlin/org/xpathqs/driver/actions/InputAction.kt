package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.extensions.isSecret
import org.xpathqs.log.style.StyleFactory
import org.xpathqs.log.style.StyledString

open class InputAction(
    var text: String,
    val to: BaseSelector,
    val clearBeforeInput: Boolean = true
) : SelectorInteractionAction(to) {

    override fun toStyledString(): StyledString {
        val value = if (to.isSecret() && text.isNotEmpty()) "'******'" else "'$text'"
        return StyledString.fromDefaultFormatString(
            Messages.Actions.Input.toString,
            value, to.name
        )
    }
}