package org.xpathqs.driver.page

import org.xpathqs.core.selector.NullSelector
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.group.GroupSelector
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.core.selector.selector.SelectorProps
import org.xpathqs.core.selector.xpath.XpathSelector

open class Page(
    base: ISelector = NullSelector(),
    open val title: String = "",
) : Block(base) {

    override val name: String
        get() = title.ifEmpty { super.name }

    override fun equals(other: Any?): Boolean {
        return name == (other as? Page)?.name ?: false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}