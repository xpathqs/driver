package org.xpathqs.driver.navigation.base

import org.xpathqs.core.selector.base.ISelector

interface IBlockSelectorNavigation {
    fun navigate(elem: ISelector)
}

abstract class BlockSelectorNavigationDelegate(
    private val base: IBlockSelectorNavigation
) : IBlockSelectorNavigation