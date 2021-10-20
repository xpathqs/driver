package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.block.Block
import org.xpathqs.driver.navigation.base.INavigableDetermination

class LoadingParser(
    private val block: Block
) {
    fun parse(): Loading {
        return Loading(
            //loadSelector = compose(*block.selectors.toTypedArray())
            loadSelectors = (block as INavigableDetermination).determination.exist
        )
    }
}