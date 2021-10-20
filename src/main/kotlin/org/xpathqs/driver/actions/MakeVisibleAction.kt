package org.xpathqs.driver.actions

import org.xpathqs.core.selector.base.BaseSelector

class MakeVisibleAction (
    val to: BaseSelector
) : SelectorInteractionAction(to)