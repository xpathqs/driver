package org.xpathqs.driver.model

import kotlin.reflect.KProperty

interface IModelComporator {
    fun isEqual(prop: KProperty<*>, v1: Any?, v2: Any?) : Boolean
}

class DefaultComparator : IModelComporator {
    override fun isEqual(prop: KProperty<*>, v1: Any?, v2: Any?): Boolean {
        return v1 == v2
    }
}

