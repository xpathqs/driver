package org.xpathqs.driver.navigation.impl

import org.xpathqs.driver.navigation.Navigator
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigableDetermination
import org.xpathqs.driver.navigation.util.Determination
import org.xpathqs.driver.navigation.util.DeterminationParser

open class NavigableDetermination(
    navigator: Navigator,
    block: INavigableDetermination
) : Navigable(navigator, block), INavigableDetermination, IBlockSelectorNavigation {
    override val determination: Determination by lazy {
        DeterminationParser(block).parse()
    }
}
