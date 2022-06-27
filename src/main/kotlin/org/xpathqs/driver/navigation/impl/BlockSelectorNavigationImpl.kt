package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator

class BlockSelectorNavigationImpl: IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator) {
        throw Exception("$elem is not present")
    }
}