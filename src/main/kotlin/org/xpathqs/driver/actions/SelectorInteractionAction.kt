package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.log.style.StyleFactory
import org.xpathqs.log.style.StyledString

abstract class SelectorInteractionAction(
    val on: BaseSelector
) : IAction {

    override fun toStyledString(): StyledString {
        return StyleFactory.text("Interaction $name, with: ") + StyleFactory.selectorName(on.name)
    }
}