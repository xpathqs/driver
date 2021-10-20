package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation

class BlockSelectorNavigationImpl: IBlockSelectorNavigation {
    override fun navigate(elem: ISelector) {
        throw Exception("$elem is not present")
    }
}