package org.xpathqs.driver.navigation.base

interface INavigableDelegate: INavigable {
    val nav: INavigable

    override fun initNavigation() {
        nav.initNavigation()
    }
    override fun addNavigation(to: INavigable, order: Int, action: (() -> Unit)?) {
        nav.addNavigation(to, order, action)
    }

    override val navOrder: Int
        get() = nav.navOrder

    override fun navigate() {
        nav.navigate()
    }
}