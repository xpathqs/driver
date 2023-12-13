package org.xpathqs.driver.navigation.base

interface IPageSpecificState {
    fun pageState(stateGroup: Int): Int
}