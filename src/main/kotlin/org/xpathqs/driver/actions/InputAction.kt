package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Messages
import org.xpathqs.driver.extensions.isSecret
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.log.style.StyleFactory
import org.xpathqs.log.style.StyledString
import java.time.Duration

open class InputAction(
    var text: String,
    val to: BaseSelector,
    var model: IBaseModel? = null,
    val clearBeforeInput: Boolean = true,
    val validateInput: Boolean = true,
    val clickSelector: BaseSelector = to,
    beforeActionDelay: Duration = Duration.ZERO,
    afterActionDelay: Duration = Duration.ZERO,
) : SelectorInteractionAction(
    on = to,
    beforeActionDelay = beforeActionDelay,
    afterActionDelay = afterActionDelay
) {
    override fun toStyledString(): StyledString {
        val value = if (to.isSecret() && text.isNotEmpty()) "'******'" else "'$text'"
        return StyledString.fromDefaultFormatString(
            Messages.Actions.Input.toString,
            value, to.name
        )
    }
}