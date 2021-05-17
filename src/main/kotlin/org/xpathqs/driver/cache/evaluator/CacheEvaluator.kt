package org.xpathqs.driver.cache.evaluator

import org.w3c.dom.Node

typealias NodesCache = HashMap<String, Collection<Node>>

open class CacheEvaluator(
    private val wrap: IEvaluator
) : IEvaluator {
    private var cache = NodesCache()

    override fun evalNodes(xpath: String): Collection<Node> {
        val cached = cache[xpath]
        if (cached != null) {
            return cached
        }

        val nodes = wrap.evalNodes(xpath)

        if (nodes.isNotEmpty()) {
            cache[xpath] = nodes
        } else {
            cache.remove(xpath)
        }

        return nodes
    }

    fun invalidate() {
        cache = NodesCache()
    }
}