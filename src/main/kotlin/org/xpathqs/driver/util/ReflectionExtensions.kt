/*
 * Copyright (c) 2021 XPATH-QS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.xpathqs.driver.util

import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.block.Block
import java.lang.reflect.Constructor
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

/**
 * Set of functional extensions to manipulate with <pre>selectors</pre> via Reflection
 */

/**
 * Check if <pre>Any</pre> is an instance of an <pre>object-class</pre>
 */
internal fun Any.isObject(): Boolean {
    if (this is Class<*>) {
        return this.declaredFields
            .find {
                it.name == "INSTANCE"
            } != null
    }
    return this.javaClass.declaredFields
        .find {
            it.name == "INSTANCE"
        } != null
}

/**
 * Check if object is a subtype of [Block]
 *
 * Requirements:
 * #1 - true when object is a subtype of [Block] but not Block directly
 * #2 - false for all others cases
 */
internal fun Any.isBlockSubtype(): Boolean {
    return this is Block && this.javaClass.superclass.simpleName != "Block"
}
/**
 * Get an Object instance of [Block] when class is an Object-class inherited from block
 */
internal fun Class<*>.getObject(): Block {
    val instanceField = this.declaredFields
        .find { it.name == "INSTANCE" } ?: throw IllegalArgumentException(
        "Provided class ${this.name} doesn't used as an object-class"
    )

    return instanceField.get(null) as? Block ?: throw IllegalArgumentException(
        "Provided class $name is not inherited from the Block class"
    )
}

/**
 * Check class for having [BaseSelector] as an inherited parent
 */
@Suppress("ReturnCount")
internal fun Class<*>.isSelectorSubtype(): Boolean {
    if (this.superclass == null) {
        return false
    }
    if (this == BaseSelector::class.java) {
        return true
    }
    return BaseSelector::class.java.isAssignableFrom(this.superclass)
            || this.isAssignableFrom(BaseSelector::class.java)
}

/**
 * Converts Kotlin Reflection property to the Java Reflection field
 */
internal fun KProperty<*>.toField() = this.javaField!!.apply {
    isAccessible = true
}


/**
 * Check if the provided object is a member of an inner class
 *
 * Require #1 - false for any objects instead of inner class members
 *
 * Require #2 - true for the inner class member
 */
internal fun Any.isInnerClass(): Boolean {
    return getInnerClassMember() != null
}

/**
 * Return member of inner class
 */
internal fun Any.getInnerClassMember(): Any? {
    val f = this::class.java.declaredFields.find {
        it.name.contains("this$")
    }
    f?.isAccessible = true
    return f?.get(this)
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> T.newInstance(): T {
    val res = if (this.isInnerClass()) {
        val c = this.getConstructor(1)
        c.newInstance(this.getInnerClassMember())
    } else {
        val c = this.getConstructor(0)
        c.newInstance()
    }

    return res as T
}

/**
 * @return constructor with provided parameterCount
 */
private fun Any.getConstructor(parameterCount: Int): Constructor<*> {
    val c = this::class.java.declaredConstructors.find {
        it.parameterCount == parameterCount
    } ?: throw IllegalArgumentException("Selector doesn't have a default constructor with $parameterCount parameter")

    c.isAccessible = true
    return c
}