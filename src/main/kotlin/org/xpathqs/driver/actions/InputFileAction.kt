package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Messages
import org.xpathqs.log.style.StyledString

open class InputFileAction(
    text: String,
    to: BaseSelector
) : InputAction(text = text, to = to, clearBeforeInput = false) {

    override fun toStyledString(): StyledString {
        return StyledString.fromDefaultFormatString(
            Messages.Actions.InputFile.toString,
            text, to.name
        )
    }
}