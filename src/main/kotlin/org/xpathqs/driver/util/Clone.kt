package org.xpathqs.driver.util

import org.apache.commons.lang3.ClassUtils
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

fun<T: Any> clone(obj: T): T {
    val res = obj.newInstance()
    res.copyProps(obj)
    return res
}

fun Any.copyProps(from: Any) {
    this::class.memberProperties
        .filterIsInstance<KMutableProperty<*>>()
        .forEach {
            val valueFrom = it.getter.call(from)!!
            val cls = valueFrom.javaClass
            if(cls.name.endsWith("String") || ClassUtils.isPrimitiveOrWrapper(cls)) {
                it.setter.call(this, valueFrom)
            } else {
                val curValue = it.getter.call(this)!!
                if(curValue.javaClass !== cls) {
                    val newObj = cls.declaredConstructors.firstOrNull {
                        it.parameterCount == 0
                    }?.newInstance() ?: cls.declaredConstructors.first().newInstance(this)
                    it.setter.call(this, newObj)
                    newObj.copyProps(valueFrom)
                } else {
                    curValue.copyProps(valueFrom)
                }
            }
        }
}