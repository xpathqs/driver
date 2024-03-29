package org.xpathqs.driver.navigation.base

import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectorBlocks
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.driver.model.IModelStates

interface IModelBlock<T: IBaseModel> {
    operator fun invoke(): T

    operator fun invoke(state: Int): T {
        return if(this is IModelStates) {
            states[state] as T
        } else {
            val m = invoke()
            m.states[state] as? T ?: m
        }
    }

    open fun getFromUi(): T {
        return invoke()
    }
}

val Block.model: IBaseModel?
    get() {
        return (this as? IModelBlock<*>)?.invoke() ?:
            this.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.invoke() ?:
            this.allInnerSelectorBlocks?.filterIsInstance<IModelBlock<*>>()?.firstOrNull()?.invoke() ?:
            (this.rootParent as? Block)?.allInnerSelectorBlocks?.filterIsInstance<IModelBlock<*>>()?.firstOrNull()?.invoke()
    }