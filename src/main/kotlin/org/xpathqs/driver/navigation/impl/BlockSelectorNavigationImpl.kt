package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.log.Log
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.INavigator

class BlockSelectorNavigationImpl: IBlockSelectorNavigation {
    override fun navigate(elem: ISelector, navigator: INavigator, model: IBaseModel) {
        if(elem is BaseSelector) {
            if(elem.isVisible) return
        }
        Log.info("There is no valid navigation callback for the $elem")
        throw XPathQsException.UnableToNavigate(elem)
    }
}