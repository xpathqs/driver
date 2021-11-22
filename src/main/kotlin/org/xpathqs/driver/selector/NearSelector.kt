package org.xpathqs.driver.selector

import org.xpathqs.core.reflection.freeze
import org.xpathqs.core.selector.args.KVSelectorArg
import org.xpathqs.core.selector.args.ValueArg
import org.xpathqs.core.selector.args.decorators.CommaDecorator
import org.xpathqs.core.selector.args.decorators.ContainsDecorator
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.extensions.core.clone
import org.xpathqs.core.selector.extensions.core.get
import org.xpathqs.core.selector.extensions.result
import org.xpathqs.core.selector.selector.Selector
import org.xpathqs.core.selector.selector.preceding
import org.xpathqs.core.selector.selector.following
import org.xpathqs.core.selector.selector.prefix
import org.xpathqs.core.selector.selector.tag
import org.xpathqs.core.selector.xpath.XpathSelector
import org.xpathqs.core.util.SelectorFactory.tagSelector
import org.xpathqs.core.util.SelectorFactory.textContainsSelector
import org.xpathqs.core.util.SelectorFactory.textSelector
import org.xpathqs.driver.cache.ICache
import org.xpathqs.driver.cache.XmlCache
import org.xpathqs.driver.mokexml.MockCachedExecutor

open class NearSelector(
    private val cache: ICache,
    val baseSelector: BaseSelector,
    val nearSelector: Selector
) : Selector() {

    //*[contains(@class, 'Organic-Subtitle') and ./../..//*[contains(text(),"светодиодные панели, светильники и лампы")]]
    private val calculatedXpath: String
        get()  {
            return getSelectorXpathInCache(nearSelector.preceding()) ?:
                getSelectorXpathInCache(nearSelector.following()) ?:
                throw Exception("Selector is Invisible")
        }

    private fun getSelectorXpathInCache(sel: Selector): String? {
        val res = baseSelector[sel].xpath
        if(cache.isPresent(base.toXpath() + res)) {
            return res
        }
        return null
    }

    override fun toXpath(): String {
        return "(${base.toXpath() + calculatedXpath})[1]" //+ props.toXpath()
    }

    override val tag: String = baseSelector.tag
}

/*
fun main() {
    println(
        NearSelector(
            XmlCache(),
            tagSelector()[
                    ContainsDecorator(
                        CommaDecorator(
                            KVSelectorArg("class", "Organic-Subtitle")
                        )
                    )
            ].freeze(),
            textContainsSelector("светодиодные панели, светильники и лампы").freeze()
        ).toXpath()
    )
}*/
