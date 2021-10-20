package org.xpathqs.driver.navigation.base

import kotlin.reflect.KProperty

object DefaultValue

class ValueDependency(
    val source: Collection<KProperty<*>>,
    val dependsOn: KProperty<*>,
    val value: Any = DefaultValue
) {
    constructor(pair: Pair<KProperty<*>, KProperty<*>>, value: Any = DefaultValue): this(listOf(pair.first), pair.second, value)
    constructor(vararg source: KProperty<*>, dependsOn: KProperty<*>, value: Any = DefaultValue): this(source.toList(), dependsOn, value)
    //constructor(pair: Pair<Collection<KProperty<*>>, KProperty<*>>, value: Any = DefaultValue): this(pair.first, pair.second, value)
}

interface IValueDependency {
    val valueDependency: Collection<ValueDependency>
}