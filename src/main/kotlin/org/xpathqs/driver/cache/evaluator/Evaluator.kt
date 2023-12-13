package org.xpathqs.driver.cache.evaluator

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xpathqs.log.Log
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

open class Evaluator(
    private val doc: Document
) : IEvaluator {
    override fun evalNodes(xpath: String): Collection<Node> {
        val res = ArrayList<Node>()
        val eval = XPathFactory.newInstance().newXPath()

        try {
            val nodes = eval.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList

            if (nodes.length > 0) {
                for (i in 0 until nodes.length) {
                    res.add(nodes.item(i))
                }
            }
        } catch (e: Exception) {
            Log.error("Evaluation problem for: $xpath")
        }

        return res
    }
}