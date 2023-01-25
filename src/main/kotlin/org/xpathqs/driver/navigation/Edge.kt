package org.xpathqs.driver.navigation

import org.jgrapht.graph.DefaultWeightedEdge
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.driver.navigation.annotations.Model.Order.Companion.DEFAULT_ORDER
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.annotations.UI.Visibility.Companion.UNDEF_STATE
import org.xpathqs.driver.navigation.base.INavigable

data class NavWrapper(
    val nav: INavigable,
    val state: Int,
    val globalState: Int = UNDEF_STATE
) {
    constructor(nav: INavigable) : this(
        nav = nav,
        state = (nav as? BaseSelector)?.findAnnotation<UI.Nav.Config>()?.defaultState ?: UNDEF_STATE
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NavWrapper) return false

        if (nav !== other.nav) return false
        if (state != other.state && state != UNDEF_STATE) return false
        if (globalState != other.globalState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 31 * nav.hashCode() + globalState
        if(state != UNDEF_STATE) {
            result += state * 10
        }
        return result
    }

    companion object {
        private val values = ArrayList<NavWrapper>()

        fun get(nav: INavigable, state: Int = UNDEF_STATE, globalState: Int = UNDEF_STATE): NavWrapper {
            val state = if(state == UNDEF_STATE) {
                (nav as? Block)?.findAnnotation<UI.Nav.Config>()?.defaultState ?: UNDEF_STATE
            } else state
            val wrapper = NavWrapper(nav, state, globalState)
            values.find { it == wrapper }?.let {
                return it
            }
            values.add(wrapper)
            return wrapper
        }
    }
}

open class Edge(
    var from: NavWrapper,
    var to: NavWrapper,
    val _weight: Double = 1.0,
    var action: (() -> Unit)? = null
) : DefaultWeightedEdge() {
    constructor(): this(
        from = NavWrapper(MockNavigable()),
        to = NavWrapper(MockNavigable()),
        action = {}
    )

    override fun getWeight() = _weight

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    private class MockNavigable : INavigable {
        override fun addNavigation(to: INavigable, weight: Int, state: Int, selfState: Int, globalState: Int, action: (() -> Unit)?) {
        }
    }
}