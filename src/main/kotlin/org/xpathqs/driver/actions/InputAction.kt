package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.extensions.isSecret

open class InputAction(
    val text: String,
    val to: BaseSelector,
    val clearBeforeInput: Boolean = true
) : SelectorInteractionAction(to) {

    override fun toString(): String {
        val value = if (to.isSecret() && text.isNotEmpty()) "******" else text
        return "Input '$value' to the ${to.name}"
    }
}