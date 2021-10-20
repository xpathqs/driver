package org.xpathqs.driver.navigation.util

import org.xpathqs.core.selector.base.BaseSelector

class Determination(
    val exist: Collection<BaseSelector> = emptyList(),
    val notExist: Collection<BaseSelector> = emptyList(),
) {
    constructor(exist: BaseSelector):
            this(exist = listOf(exist))
    constructor(exist: BaseSelector, notExist: BaseSelector):
            this(exist = listOf(exist), notExist = listOf(notExist))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Determination) return false

        if (exist != other.exist) return false
        if (notExist != other.notExist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = exist.hashCode()
        result = 31 * result + notExist.hashCode()
        return result
    }
}