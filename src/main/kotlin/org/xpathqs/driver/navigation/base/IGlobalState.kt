package org.xpathqs.driver.navigation.base

interface IGlobalState {
    var globalState: Int
}

object NoGlobalState : IGlobalState {
    override var globalState: Int = 0
}