package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector

open class ClearAction(
    selector: BaseSelector,
    val clickSelector: BaseSelector
) : SelectorInteractionAction(selector)