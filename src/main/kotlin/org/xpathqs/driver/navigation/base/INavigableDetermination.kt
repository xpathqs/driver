package org.xpathqs.driver.navigation.base

import org.xpathqs.driver.extensions.isHidden
import org.xpathqs.driver.extensions.isVisible
import org.xpathqs.driver.navigation.util.Determination

interface INavigableDetermination : INavigable {
    open val determination: Determination
    open val descriptor: String
        get() = ""
}

val INavigableDetermination.isVisible: Boolean
    get() {
        return determination.exist.none { it.isHidden }
                && determination.notExist.none { it.isVisible }
    }