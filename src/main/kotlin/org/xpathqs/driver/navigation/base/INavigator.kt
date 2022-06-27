package org.xpathqs.driver.navigation.base

interface INavigator {
    fun navigate(from: INavigable, to: INavigable)
    val currentPage: INavigableDetermination
}