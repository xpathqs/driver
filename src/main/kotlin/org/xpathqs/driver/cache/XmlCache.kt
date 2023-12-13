package org.xpathqs.driver.cache

import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xpathqs.driver.cache.evaluator.AttributeEvaluator
import org.xpathqs.driver.cache.evaluator.CacheEvaluator
import org.xpathqs.driver.cache.evaluator.Evaluator
import org.xpathqs.log.Log
import java.io.StringReader
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

open class XmlCache : Cache() {

    protected lateinit var doc: Document
    protected var factory = DocumentBuilderFactory.newInstance()
    protected lateinit var builder: DocumentBuilder

    override fun update(xml: String) {
        if (xml == "") {
            Log.error("Page Source can't be empty, something went wrong, unable to continue...")
            return
        } else {
            Log.info("cache was updated")
        }

        this.xml = getCleanedXml(xml)

        try {
            val input = InputSource(StringReader(this.xml))
            input.encoding = "UTF-8"

            builder = factory.newDocumentBuilder()
            doc = builder.parse(input)

            evaluator = CacheEvaluator(Evaluator(doc))
            attributeEvaluator = AttributeEvaluator(evaluator)
        } catch (e: Exception) {
            Log.error("Set xml error:\n ${this.xml}")
            e.printStackTrace()
        }
    }

    private fun getCleanedXml(xmlString: String): String {
        val XML_ENTITY_PATTERN = Pattern.compile("\\&\\#(?:x([0-9a-fA-F]+)|([0-9]+))\\;")

        fun isInvalidXmlChar(char: Int): Boolean {
            return !(char == 0x9 || char == 0xA || char == 0xD ||
                    char in 0x20..0xD7FF ||
                    char in 0x10000..0x10FFFF)
        }

        val m = XML_ENTITY_PATTERN.matcher(xmlString)
        val replaceSet = HashSet<String>()

        while (m.find()) {
            val group = m.group(1)
            val group2 = m.group(2)
            val char: Int
            if (group != null) {
                char = Integer.parseInt(group, 16)
                if (isInvalidXmlChar(char)) {
                    replaceSet.add("&#x$group;")
                }
            } else if (group2 != null) {
                char = Integer.parseInt(group2)
                if (isInvalidXmlChar(char)) {
                    replaceSet.add("&#$group2;")
                }
            }
        }
        var cleanedXmlString = xmlString
        for (replacer in replaceSet) {
            cleanedXmlString = cleanedXmlString.replace(replacer.toRegex(), "?")
        }

        return cleanedXmlString
    }

    override fun clear() {
        clearXml()
        (evaluator as? CacheEvaluator)?.invalidate()
    }

    protected fun clearXml() {
        update(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <hierarchy rotation="0"></hierarchy>
            """.trimIndent()
        )
    }

    init {
        clearXml()
    }
}