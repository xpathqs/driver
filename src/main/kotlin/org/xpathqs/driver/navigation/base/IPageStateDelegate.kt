package org.xpathqs.driver.navigation.base

import org.xpathqs.driver.navigation.impl.PageState

interface IPageStateDelegate : IPageState {
    val ps: PageState

    override val pageState: Int
        get() = ps.pageState
}