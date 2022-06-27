package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible

class Loading(
    var loadSelector: BaseSelector? = null,
    var loadSelectors: Collection<BaseSelector> = emptyList()
) {
    val isLoaded: Boolean
        get() {
            return if(loadSelector != null) {
                loadSelector!!.isVisible
            } else {
                loadSelectors.none { it.isHidden }
            }
        }
}