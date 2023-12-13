package org.xpathqs.driver.util

import org.xpathqs.core.selector.base.BaseSelector

interface IStateFilter {
    fun filter(col: Collection<BaseSelector>, state: Int) : Collection<BaseSelector>
}