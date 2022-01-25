package org.xpathqs.driver.navigation.impl

import org.xpathqs.core.selector.base.*
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.core.selector.extensions.isChildOf
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.extensions.click
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.IBlockSelectorNavigation
import org.xpathqs.driver.navigation.base.IModelBlock
import org.xpathqs.driver.widgets.IFormSelect

class ModelDependencyNavigation(
    private val base: IBlockSelectorNavigation
): IBlockSelectorNavigation {
    override fun navigate(elem: ISelector) {
        if(elem is BaseSelector) {
            val blockWithModel = elem.parents.filterIsInstance<IModelBlock<*>>() as? IModelBlock<*>
            if(blockWithModel != null) {
                val model = blockWithModel.invoke()
                val p = model.findPropBySel(elem)

            }

            val selectable = elem.findParentWithAnnotation(UI.Widgets.Form::class)


        }

        return base.navigate(elem)
    }
}