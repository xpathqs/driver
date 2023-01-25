package org.xpathqs.driver.cache.evaluator

import org.w3c.dom.Node

interface IEvaluator {
    fun evalNodes(xpath: String): Collection<Node>

    fun evalNode(xpath: String) = evalNodes(xpath).firstOrNull()
    fun hasNodes(xpath: String) = evalNodes(xpath).isNotEmpty()
}