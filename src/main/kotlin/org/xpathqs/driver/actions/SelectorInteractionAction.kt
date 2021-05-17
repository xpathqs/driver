package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector

abstract class SelectorInteractionAction(
    val on: BaseSelector
) : IAction {

    override fun toString(): String {
        return "Interaction $name, with: ${on.name}"
    }
}