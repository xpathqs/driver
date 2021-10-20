package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.NullSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.block.Block

open class BlockNavigation(
    val defaultVisibility: Boolean = false,
    val byClick: ISelector = NullSelector(),
    val byCheckbox: ISelector = NullSelector()
) {
}

class NullBlockNavigation: BlockNavigation()

interface IBlockNavigation {
    val selfNavigation: BlockNavigation
}
