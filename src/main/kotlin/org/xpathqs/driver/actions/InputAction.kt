package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector

open class InputAction(
    val text: String,
    val to: BaseSelector
) : SelectorInteractionAction(to) {
}