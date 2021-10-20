package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.BaseSelector

class Loading(
    var loadSelector: BaseSelector? = null,
    var loadSelectors: Collection<BaseSelector> = emptyList()
) {
}