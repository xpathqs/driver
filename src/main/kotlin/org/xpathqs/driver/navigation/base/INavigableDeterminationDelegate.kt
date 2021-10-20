package org.xpathqs.driver.navigation.base

import org.xpathqs.driver.navigation.util.Determination

interface INavigableDeterminationDelegate : INavigableDelegate, INavigableDetermination {
    override val nav: INavigableDetermination

    override val determination: Determination
        get() = nav.determination

    override val descriptor: String
        get() = nav.descriptor
}