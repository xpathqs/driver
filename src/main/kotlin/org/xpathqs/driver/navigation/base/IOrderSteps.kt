package org.xpathqs.driver.navigation.base

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties


enum class InputType{
    INPUT, SUBMIT, CLICK
}

class InputAction(
    val type: InputType = InputType.SUBMIT,
    val props: Collection<KProperty<*>>) {

    companion object {
        fun submit(cls: KClass<*>) = submit(*cls.declaredMemberProperties.filterIsInstance<KMutableProperty<*>>().toTypedArray())
        fun submit(vararg props: KProperty<*>)
                = InputAction(InputType.SUBMIT, listOf(*props))

        fun input(cls: KClass<*>) = input(*cls.declaredMemberProperties.filterIsInstance<KMutableProperty<*>>().toTypedArray())
        fun input(vararg props: KProperty<*>)
                = InputAction(InputType.INPUT, listOf(*props))

        fun click(cls: KClass<*>) = click(*cls.declaredMemberProperties.filterIsInstance<KMutableProperty<*>>().toTypedArray())
        fun click(vararg props: KProperty<*>)
                = InputAction(InputType.CLICK, listOf(*props))
    }
}

interface IOrderedSteps {
    val steps: Collection<InputAction>
}