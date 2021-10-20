package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.constants.Messages
import org.xpathqs.log.style.StyleFactory.selectorName
import org.xpathqs.log.style.StyleFactory.text
import org.xpathqs.log.style.StyledString

open class ScreenShotAction(
    val sel: BaseSelector,
    val boundRect: Boolean = true
) : IAction {
    override fun toStyledString() =
        text("Taking a Screenshot of") + selectorName(sel.name)
}