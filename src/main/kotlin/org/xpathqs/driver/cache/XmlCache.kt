package org.xpathqs.driver.cache

import org.xpathqs.driver.log.Log
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.regex.Pattern

class XmlCache : Cache() {
    override fun setXml(xml: String, clear: Boolean) {
        if (xml == "") {
            Log.error("Page Source can't be empty, something went wrong, unable to continue...")
        } else {
            Log.log("cache was updated")
        }

        if (this.xml != null) {
            this.prevXml = this.xml!!
        }

        if (clear) {
            nodesMap.clear()
        }

        this.xml = getCleanedXml(xml)

        try {
            var input = InputSource(StringReader(this.xml))
            input.encoding = "UTF-8"

            builder = factory.newDocumentBuilder()
            doc = builder.parse(input)
        } catch (e: Exception) {
            Log.error("Set xml error:\n ${this.xml}")
            e.printStackTrace()
        }

    }

    private val XML_ENTITY_PATTERN = Pattern.compile("\\&\\#(?:x([0-9a-fA-F]+)|([0-9]+))\\;")

    private fun getCleanedXml(xmlString: String): String {
        fun isInvalidXmlChar(`val`: Int): Boolean {
            return !(`val` == 0x9 || `val` == 0xA || `val` == 0xD ||
                    `val` in 0x20..0xD7FF ||
                    `val` in 0x10000..0x10FFFF)
        }

        val m = XML_ENTITY_PATTERN.matcher(xmlString)
        val replaceSet = HashSet<String>()

        while (m.find()) {
            var group = m.group(1)
            var group2 = m.group(2)
            val `val`: Int
            if (group != null) {
                `val` = Integer.parseInt(group, 16)
                if (isInvalidXmlChar(`val`)) {
                    replaceSet.add("&#x$group;")
                }
            } else if (group2 != null) {
                `val` = Integer.parseInt(group2)
                if (isInvalidXmlChar(`val`)) {
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
}