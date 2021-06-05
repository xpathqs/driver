package org.xpathqs.driver.util

import org.xpathqs.core.model.ISelectorValueExtractor
import org.xpathqs.core.model.Model
import org.xpathqs.core.model.ModelAssociation
import org.xpathqs.core.model.ValueSetter
import org.xpathqs.core.selector.block.Block
import org.xpathqs.driver.extensions.text

object ModelHelper {

    class TextExtractor : ISelectorValueExtractor {
        override fun apply(assoc: ModelAssociation): String {
            return assoc.sel.text
        }
    }

    inline fun <reified T: Any> Block.getModel() : T {
        val model = createInstance<T>()

        ValueSetter(
            Model(T::class.java, this).associations,
            TextExtractor()
        ).init(model)

        return model
    }

    inline fun <reified T> createInstance() =
         T::class.java.declaredConstructors
            .find { it.parameterCount == 0 }
                ?.newInstance() as T
             ?: throw IllegalArgumentException("No Default constructor for the model")

}