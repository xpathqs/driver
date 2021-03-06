package org.xpathqs.driver.widgets

import org.apache.commons.lang3.ClassUtils
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KMutableProperty

object Obj1 {
    val s = "asd"

    object Obj2 {
        val s = "asd"
    }
}

val KProperty<*>.isPrimitive: Boolean
    get() {
        return this.name.endsWith("String") || ClassUtils.isPrimitiveOrWrapper(this.javaClass)
    }

fun findParent(source: Any, prop: KProperty<*>): Any? {
    source::class.members.forEach {
        if(it.name == prop.name) {
            return source
        }
        if((it as? KProperty<*>)?.isPrimitive == false) {
            val res = findParent(it.getter.call(source)!!, prop)
            if(res != null) {
                return res
            }
        }
    }
    return null
}

/*
fun main() {
    println(
        findParent(Obj1, Obj1.Obj2::s)
    )
}*/


class Example {
    var sto1 = "s1"
    var sto2 = "s2"
}

fun main() {
    val obj = Example()
    val userInput = "1"

    val prop = Example::class.memberProperties.find { it.name == "sto$userInput"}
    prop as KMutableProperty<*>
    //get value example
    println(prop.get(obj))

    //set value example
    prop.setter.call(obj, "new value")
    println(prop.get(obj))
}