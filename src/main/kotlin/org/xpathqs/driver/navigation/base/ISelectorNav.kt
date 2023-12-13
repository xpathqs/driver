package org.xpathqs.driver.navigation.base

import org.xpathqs.core.selector.base.BaseSelector

interface ISelectorNav {
    fun navigateDirectly(to: BaseSelector)
}