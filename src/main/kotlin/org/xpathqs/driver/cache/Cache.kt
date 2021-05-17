package org.xpathqs.driver.cache

import org.xpathqs.driver.cache.evaluator.AttributeEvaluator
import org.xpathqs.driver.cache.evaluator.IEvaluator

abstract class Cache : ICache {
    protected var xml: String = ""

    protected lateinit var evaluator: IEvaluator
    protected lateinit var attributeEvaluator: AttributeEvaluator

    override fun isPresent(xpath: String): Boolean {
        if (xpath.isEmpty()) {
            return false
        }

        return evaluator.hasNodes(xpath)
    }

    override fun getElementsCount(xpath: String) = evaluator.evalNodes(xpath).size

    override fun getAttribute(xpath: String, name: String) = attributeEvaluator.getAttribute(xpath, name)

    override fun getAttributes(xpath: String, name: String) = attributeEvaluator.getAttributes(xpath, name)
}