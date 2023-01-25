package org.xpathqs.driver.cache.evaluator

import org.w3c.dom.Node
import org.xpathqs.driver.constants.Global

class AttributeEvaluator(
    private val eval: IEvaluator
) {
    fun getAttribute(xpath: String, name: String) =
        getAttributeFromNode(eval.evalNode(xpath), name)

    fun getAttributes(xpath: String): Collection<Pair<String, String>> {
        val res = ArrayList<Pair<String, String>>()
        val node = eval.evalNode(xpath)
        if(node != null) {
            for (i in 0 until node.attributes.length) {
                val item = node.attributes.item(i)
                res.add(item.nodeName to item.nodeValue)
            }
        }
        return res
    }

    fun getAttributes(xpath: String, name: String): Collection<String> {
        val res = ArrayList<String>()

        eval.evalNodes(xpath).forEach {
            res.add(getAttributeFromNode(it, name))
        }

        return res
    }

    private fun getAttributeFromNode(node: Node?, name: String): String {
        if(node != null) {
            if (name == Global.TEXT_ARG) {
                return node.textContent
            }

            return node.attributes
                .getNamedItem(fixParamName(name)).textContent
        } else {
            return ""
        }
    }

    private fun fixParamName(name: String) =
        if (name.startsWith("@")) name.removeRange(0, 1) else name
}