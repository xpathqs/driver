package org.xpathqs.driver.navigation.base

interface INavigableDelegate: INavigable {
    val nav: INavigable

    override fun initNavigation() {
        nav.initNavigation()
    }
    override fun addNavigation(
        to: INavigable,
        order: Int,
        selfState: Int,
        state: Int,
        globalState: Int,
        action: (() -> Unit)?) {
        nav.addNavigation(
            to,
            order,
            selfState,
            state,
            globalState,
            action
        )
    }

    override val navOrder: Int
        get() = nav.navOrder

    override fun navigate(state: Int) {
        nav.navigate(state)
    }
}