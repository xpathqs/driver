package org.xpathqs.driver.cache

import org.xpathqs.core.selector.base.ISelector
import org.xpathqs.core.selector.compose.ComposeSelector
import org.xpathqs.driver.log.Log
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xpathqs.core.selector.NullSelector
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

abstract class Cache : ICache {
    protected var xml: String? = null
    protected var prevXml = ""

    protected val nodesMap = HashMap<String, Node?>()
    protected var doc: Document? = null
    internal var factory = DocumentBuilderFactory.newInstance()
    lateinit var builder: DocumentBuilder

    override fun isVisible(selector: ISelector): Boolean {
        if (selector is NullSelector) return false
        if (selector is ComposeSelector) {
            if (selector.toXpath().isEmpty()) {
                return false
            }
        }
        return isVisible(selector.toXpath())
    }

    override fun isVisible(selectorXpath: String): Boolean {
        val xpath = XPathFactory.newInstance().newXPath()

        try {
            val nodes = xpath.evaluate(selectorXpath, doc, XPathConstants.NODESET) as NodeList

            //  Log.detail("Checking visibility (Cache) of: $selectorXpath")
            if (nodes.length > 0) {
                nodesMap[selectorXpath] = nodes.item(0)
                //      Log.detail("visible")
                return true
            } else {
                nodesMap[selectorXpath] = null
                //    Log.detail("hidden")
            }
        } catch (e: Exception) {
            Log.error("Can't parse xpath: $selectorXpath")
        }

        return false
    }

    override fun getElementsCount(selectorXpath: String): Int {
        val xpath = XPathFactory.newInstance().newXPath()

        try {
            return (xpath.evaluate(selectorXpath, doc, XPathConstants.NODESET) as NodeList).length
        } catch (e: Exception) {
            Log.error("Can't parse xpath: $selectorXpath")
        }

        return 0
    }

    override fun getAttribute(selectorXpath: String, attr: String, default: String): String? {
        val xpath = XPathFactory.newInstance().newXPath()
        val attr = if (attr.startsWith("@")) attr.removeRange(0, 1) else attr

        try {
            val nodes = xpath.evaluate(selectorXpath, doc, XPathConstants.NODESET) as NodeList

            if (nodes.length > 0) {
                nodesMap[selectorXpath] = nodes.item(0)

                val textContent = if (attr.startsWith("text")) nodesMap[selectorXpath]?.textContent else default

                return nodesMap[selectorXpath]?.attributes?.getNamedItem(attr)?.textContent
                    ?: textContent
            } else {
                nodesMap[selectorXpath] = null
            }
        } catch (e: Exception) {
            Log.error("Can't parse xpath: $selectorXpath")
        }

        return default
    }

    override fun getAttributes(selectorXpath: String, attr: String): List<String> {
        val xpath = XPathFactory.newInstance().newXPath()
        val attr = if (attr.startsWith("@")) attr.removeRange(0, 1) else attr
        val res = ArrayList<String>()

        try {
            val nodes = xpath.evaluate(selectorXpath, doc, XPathConstants.NODESET) as NodeList

            if (nodes.length > 0) {
                for (i in 0 until nodes.length) {
                    nodesMap[selectorXpath] = nodes.item(i)
                    val textContent = if (attr.startsWith("text")) nodesMap[selectorXpath]?.textContent else ""
                    val text = nodesMap[selectorXpath]?.attributes?.getNamedItem(attr)?.textContent ?: textContent
                    if (text!!.isNotEmpty()) {
                        res.add(text)
                    }
                }
            } else {
                nodesMap[selectorXpath] = null
            }
        } catch (e: Exception) {
            Log.error("Can't parse xpath: $selectorXpath")
        }

        return res
    }

    override fun clear(clearXml: Boolean) {
        if (clearXml) {
            setXml(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<hierarchy rotation=\"0\">\n" +
                        "</hierarchy>", true
            )
        }

        nodesMap.clear()
    }
}