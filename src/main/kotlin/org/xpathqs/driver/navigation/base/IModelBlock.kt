package org.xpathqs.driver.navigation.base

import org.xpathqs.core.selector.base.findAnyParentAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectorBlocks
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.widgets.IBaseModel
import kotlin.reflect.full.createInstance

interface IModelBlock<T: IBaseModel> {
    operator fun invoke(): T

    operator fun invoke(state: Int): T {
        val m = invoke()
        return m.states[state] as? T ?: m
    }
}

val Block.model: IBaseModel?
    get() {
        val form = this.findAnyParentAnnotation<UI.Widgets.Form>()
        return form?.model?.createInstance() ?:
            (this as? IModelBlock<*>)?.invoke() ?:
            this.parents.filterIsInstance<IModelBlock<*>>().firstOrNull()?.invoke() ?:
            this.allInnerSelectorBlocks?.filterIsInstance<IModelBlock<*>>()?.firstOrNull()?.invoke() ?:
            (this.rootParent as? Block)?.allInnerSelectorBlocks?.filterIsInstance<IModelBlock<*>>()?.firstOrNull()?.invoke()
    }