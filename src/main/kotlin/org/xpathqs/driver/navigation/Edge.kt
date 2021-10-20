package org.xpathqs.driver.navigation

import org.jgrapht.graph.DefaultWeightedEdge
import org.xpathqs.driver.navigation.annotations.Model.Order.Companion.DEFAULT_ORDER
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.INavigable

open class Edge(
    var from: INavigable,
    var to: INavigable,
    val _weight: Double = 1.0,
    var action: (() -> Unit)? = null
) : DefaultWeightedEdge() {
    constructor(): this(
        from = MockNavigable(),
        to = MockNavigable(),
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
        override fun addNavigation(to: INavigable, weight: Int, action: (() -> Unit)?) {
        }
    }
}