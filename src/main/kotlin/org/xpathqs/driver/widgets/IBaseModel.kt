package org.xpathqs.driver.widgets

import org.apache.commons.lang3.ClassUtils
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.base.findAnnotation
import org.xpathqs.core.selector.base.findParentWithAnnotation
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.selector.block.allInnerSelectors
import org.xpathqs.core.selector.block.findWithAnnotation
import org.xpathqs.core.selector.extensions.parents
import org.xpathqs.core.selector.extensions.rootParent
import org.xpathqs.core.selector.extensions.simpleName
import org.xpathqs.core.selector.extensions.text
import org.xpathqs.driver.exceptions.XPathQsException
import org.xpathqs.driver.extensions.*
import org.xpathqs.driver.log.Log
import org.xpathqs.driver.navigation.annotations.Model
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.navigation.base.*
import org.xpathqs.driver.util.newInstance
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.properties.Delegates
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmName

open class IBaseModel(
    private val view: Block? = null
) {
    open val mappings: LinkedHashMap<KProperty<*>, BaseSelector>
        by lazy {
            val res = LinkedHashMap<KProperty<*>, BaseSelector>()
            res.putAll(reflectionMappings)
            res.putAll(propArgsMappings)

            res
        }

    open val reflectionMappings: LinkedHashMap<KProperty<*>, BaseSelector>
        by lazy {
            val res = LinkedHashMap<KProperty<*>, BaseSelector>()

            val c = this::class.java.declaredFields
            val orderById = c.withIndex().associate { it.value.name to it.index }
            val p = this::class.declaredMemberProperties
            val sorted = p.sortedBy { orderById[it.name] }
            sorted.forEach { p ->
                view?.allInnerSelectors?.find { it.simpleName == p.name }?.let { s ->
                    res[p] = s
                }
            }
            res
        }

    open val propArgsMappings: LinkedHashMap<KProperty<*>, BaseSelector>
        by lazy {
            val before = isInit.get() == true
            isInit.set(false)

            allProperties().forEach { p ->
                val parent = findParent(this, p)
                try {
                    val v = p.getter.call(parent)
                    (p as? KMutableProperty<*>)?.setter?.call(parent, v)
                } catch (e: Exception) {
                    Log.error("No value for the '${p.name}'\n${e.message}")
                }
            }

            isInit.set(before)
            lazyPropMap
        }

    private val lazyPropMap = LinkedHashMap<KProperty<*>, BaseSelector>()

    open val states: Map<Int, IBaseModel>
        get() = emptyMap()

    val containers: Collection<BaseSelector>
        get() = mappings.values
            .distinctBy { it.rootParent }
            .mapNotNull { it.rootParent as? BaseSelector }

    fun findWidget(ann: KClass<*>) =
        containers.map {
            if(it.annotations.find{ it.annotationClass == ann} != null) it else
            (it as? Block)?.findWithAnnotation(ann)
            ?: it.findParentWithAnnotation(ann)
        }?.first()

    open fun beforeSubmit() {}
    open fun afterSubmit() {}

    private val filledProps = HashSet<KProperty<*>>()

    private var submitCalled = false
    open fun submit() {
        submitCalled = false

      //  if(!propTrig) {
            fill()
      //  }
        beforeSubmit()
        if(!submitCalled) {
            findWidget(UI.Widgets.Submit::class)?.click()
        }
        afterSubmit()
        val p = findWidget(UI.Nav.PathTo::class)
        println(p)
        if(p != null) {
            val pathTo = p.findAnnotation<UI.Nav.PathTo>()?.bySubmit?.objectInstance
            if(pathTo is ILoadable) {
                pathTo.waitForLoad(Duration.ofSeconds(30))
            }
        }
    }

    open fun reset() {
        mappings.keys.forEach {
            (it as KMutableProperty<*>).setter.call(this, "")
        }
    }

    open fun invalidate(sel: BaseSelector) {
        val prop = findPropBySel(sel)!!
        val ann = prop.findAnnotation<Validation>()
        if (ann == null) {
            if (prop is KMutableProperty<*>) {
                val targetSel = findSelByProp(prop) as? Block
                val hasChildFile = targetSel?.allInnerSelectors?.firstOrNull {
                    it.findAnnotation<UI.Widgets.Input>()?.type != "file"
                } != null
                val hasFile = targetSel?.findAnnotation<UI.Widgets.Input>()?.type != "file"
                if(!hasChildFile && !hasFile) {
                    val parent = findParent(this, prop)
                    try {
                        prop.setter.call(parent, "")
                    } catch (e: Exception) {
                        println(e)
                        //     throw e
                    }catch (e: Error) {
                        println(e)
                        //    throw e
                    }
                }

                if (sel.isHidden) {
                    mappings.values.find { it.name != sel.name }?.click()
                }
                if (sel.isHidden) {
                    submit()
                }
            }
        }
    }

    open fun fill(prop: KMutableProperty<*>) {
        val parent = findParent(this, prop)
        val v = prop.getter.call(parent)

        prop.setter.call(parent, v)

       /* if(prop.isPrimitive && v != null) {
            val sel = mappings.filterKeys { it.name == prop.name }.values.first()

            Log.action("Selector ${sel.name} was found, calling input") {
                makeVisible(sel, prop)
                if (sel is IFormInput) {
                    if(v is String && v.isEmpty()) {
                        sel.clear()
                    } else {
                        sel.input(v.toString())
                    }
                } else {
                    sel.input(v.toString())
                }
            }
        } else {
            prop.setter.call(parent, v)
        }*/
    }

    open fun fill() {
        filledProps.clear()
        if(this is IOrderedSteps) {
            evalActions(steps)
        } else {
            this.mappings.keys.forEach {
                fill(it as KMutableProperty<*>)
            }
        }

        this.propTrig = true
    }

    fun evalActions(steps: Collection<InputAction>) {
        steps.forEach {
            val action = if(it is SwitchInputAction)
                if(it.func()) it.onTrue else it.onFalse
            else it
            action.props.forEach {
                if(it.getter.parameters.isEmpty()) {
                    val v = it.getter.call()
                    (it as KMutableProperty<*>).setter.call(v)
                } else {
                    val parent = findParent(this, it)
                    try {
                        val v = it.getter.call(parent)
                        (it as KMutableProperty<*>).setter.call(parent, v)
                    } catch (e: Exception) {
                        val v = it.getter.call(parent)
                        (it as KMutableProperty<*>).setter.call(parent, v)
                    }
                }
            }

            Thread.sleep(500) //short delay after input action
            if(action.type == InputType.SUBMIT) {
                Thread.sleep(500)
                (getSelector(action).rootParent as Block).findWithAnnotation(
                    UI.Widgets.Submit::class
                )?.waitForVisible()?.click() ?: throw Exception("No Submit Widget button")
                submitCalled = true
            }
        }
    }

    private fun getSelector(action: InputAction): BaseSelector {
        return this.mappings.filterKeys {
            it.name == (action.props as List).firstOrNull()?.name
        }.values.firstOrNull()
            ?: throw Exception("No Selector for the action")
    }

    open fun submit(state: Int) {
        if(state == DEFAULT) {
            submit()
        } else {
            states[state]?.let {
                //it.fill()
                it.submit()
            }
        }
    }

    open fun submit(page: INavigable) {
        if(this is IOrderedSteps) {
            val stepsToSubmit = ArrayList<InputAction>()
            steps.forEach { action ->
                if(action.type == InputType.DYNAMIC) {
                    stepsToSubmit.add(action)
                } else {
                    val sel = getSelector(action)
                    page as BaseSelector
                    if(sel.parents.find { it.name == page.name} != null) {
                        stepsToSubmit.add(action)
                    }
                }
            }
            evalActions(stepsToSubmit)
        } else {
            if(states.containsKey(CORRECT)) {
                submit(CORRECT)
            } else {
                submit()
            }
        }
    }

    fun findSelByProp(prop: KProperty<*>) =
        mappings.filterKeys { it.name == prop.name }.values.first()

    fun findPropBySel(sel: BaseSelector): KProperty<*>? {
        val res = mappings.filterValues {
            it.name == sel.name
        }
        if (res.isNotEmpty()) {
            return res.keys.firstOrNull()
        }

        val input = (sel.base as? Block)?.findWithAnnotation(UI.Widgets.Input::class)
        if (input != null) {
            return mappings.filterValues {
                it.name == input.name || it == input.base
            }.keys.firstOrNull()
        }

        return null
    }

    private var propTrig = false

    fun findParent(source: Any, prop: KProperty<*>): Any? {
       // return Log.action("Finding parent for ${prop.name}") {
            try {
                properties(source).forEach {
                    if(it.name == prop.name || it === prop) {
                        return source
                    }
                    if(!it.isPrimitive) {
                        try {
                            it.isAccessible = true
                        } catch (e:  Error) {

                        }catch (e:  Exception) {

                        }
                        var res: Any? = null
                        try {
                            res = findParent(it.getter.call(source)!!, prop)
                        } catch (e:  Exception) {

                        } catch (e: Error) {

                        }
                        if(res != null) {
                            return res
                        }
                    }
                }
            } catch (e: Exception) {

            }

            return null
      //  }

    }

    fun properties(obj: Any = this) = obj::class.memberProperties.filter {
        it is KMutableProperty<*>
                || it.returnType.javaType.typeName.startsWith(obj::class.jvmName)
    }

    fun allProperties(obj: Any = this): Collection<KProperty<*>> {
        val res = ArrayList<KProperty<*>>()

        res.addAll(
            obj::class.memberProperties.filterIsInstance<KMutableProperty<*>>()
        )

        obj::class.memberProperties.filter {
            it.returnType.javaType.typeName.startsWith(obj::class.jvmName)
        }.forEach {
            val v = it.getter.call(this)!!
            res.addAll(
                allProperties(v)
            )
        }

        if (res.isEmpty()) throw XPathQsException.ModelDoesntHaveMutableProps(this)

        val c = this::class.java.declaredFields
        val orderById = c.withIndex().associate { it.value.name to it.index }

        return res.sortedBy { orderById[it.name] }
    }

    fun makeVisible(sel: BaseSelector, prop: KProperty<*>) {
        val p = findParent(this@IBaseModel, prop)
        if(sel.isHidden) {
            Log.action("Selector $sel is hidden") {
                if(p is IValueDependency) {
                    val vd = p.valueDependency.find { vd ->
                        vd.source.find { it.name == prop.name } != null
                    }

                    val prop = vd?.dependsOn as? KMutableProperty<*>
                    if(prop != null) {
                        Log.action("Dependency was found of: ${prop.name}") {
                            if(prop.getter.parameters.size == 1) {
                                val thiz = findParent(this@IBaseModel, prop)
                                if(vd!!.value is DefaultValue) {
                                    val v = prop.getter.call(thiz)
                                    prop.setter.call(thiz, v)
                                } else {
                                    prop.setter.call(thiz, vd!!.value)
                                }
                                //     Thread.sleep(500)
                                vd.source.forEach {
                                    findSelByProp(it).waitForVisible()
                                }
                            } else {
                                if(vd!!.value is DefaultValue) {
                                    val v = prop.getter.call()
                                    prop.setter.call(v)
                                } else {
                                    prop.setter.call(vd!!.value)
                                }
                                //            Thread.sleep(500)
                                vd.source.forEach {
                                    findSelByProp(it).waitForVisible()
                                }
                            }
                        }
                    } else {
                        if(this@IBaseModel is IValueDependency) {
                            val parentProp = this@IBaseModel::class.memberProperties.firstOrNull {
                                it.call(this) === p
                            }

                            val vd = this.valueDependency.find { vd ->
                                vd.source.find { it.name == parentProp?.name } != null
                            }

                            if(vd != null && parentProp != null) {
                                Log.action("Dependency was found for parent: ${parentProp.name}") {
                                    val member = this.valueDependency.find { vd ->
                                        vd.source.find { it.name == parentProp?.name } != null
                                    }?.dependsOn
                                    if(member != null) {
                                        if(this is IOrderedSteps) {
                                            val obj = member.getter.call()!!
                                            val action = this.steps.find {
                                                it.props.containsAll(
                                                    obj::class.members.filterIsInstance<KMutableProperty<*>>()
                                                )
                                            }
                                            if(action != null) {
                                                evalActions(listOf(action))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if(this@IBaseModel is IValueDependency) {
                        val parentProp = this@IBaseModel::class.memberProperties.firstOrNull {
                            it.call(this) === p
                        }

                        val vd = this.valueDependency.find { vd ->
                            vd.source.find { it.name == parentProp?.name } != null
                        }

                        if(vd != null && parentProp != null) {
                            Log.action("Dependency was found for parent: ${parentProp.name}") {
                                val member = this.valueDependency.find { vd ->
                                    vd.source.find { it.name == parentProp?.name } != null
                                }?.dependsOn
                                if(member != null) {
                                    if(this is IOrderedSteps) {
                                        val obj = member.getter.call()!!
                                        val action = this.steps.find {
                                            it.props.containsAll(
                                                obj::class.members.filterIsInstance<KMutableProperty<*>>()
                                            )
                                        }
                                        if(action != null) {
                                            evalActions(listOf(action))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(sel.isHidden) {
            sel.makeVisible()
        }
    }

    inner class FieldsCls {
        fun input(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) { prop, old, new ->
                // if(old != new) {

                    val ts1 = System.currentTimeMillis()
                    propTrig = true
                    mapping?.let {
                        lazyPropMap[prop] = it
                    }

                    if(isInit.get() == true) {
                        if(filledProps.contains(prop)) {
                            return@observable
                        }

                        val sel = mappings.filterKeys { it.name == prop.name }.values.first()
                        makeVisible(sel, prop)

                        if(ignoreInput.get().peek() !== prop) {
                            Log.action("Selector ${sel.name} was found, calling input") {
                                if(sel.isHidden) {
                                    sel.waitForVisible(Duration.ofSeconds(1))
                                    if(sel.isHidden) {
                                        throw Exception("Selector can't be hidden")
                                    }
                                }
                                if(sel is IFormSelect && new.isEmpty()) {
                                    sel.selectAny()
                                } else if (sel is IFormInput) {
                                    if(new.isEmpty()) {
                                        sel.clear()
                                    } else {
                                        sel.input(new)
                                    }
                                } else {
                                    sel.input(new)
                                }
                                filledProps.add(prop)
                            }
                        }


                    }

                //}
            }

        fun nothing(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) {
                    prop, old, new ->
                mapping?.let {
                    lazyPropMap[prop] = it
                }

                if(isInit.get() == true) {
                    val sel = mappings.filterKeys { it.name == prop.name }.values.first()
                    makeVisible(sel, prop)
                    filledProps.add(prop)
                }
            }

        fun click(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) { prop, old, new ->
                propTrig = true
                mapping?.let {
                    lazyPropMap[prop] = it
                }
                if(isInit.get() == true) {
                    val sel = mappings.filterKeys { it.name == prop.name }.values.first()

                    makeVisible(sel, prop)
                    if(ignoreInput.get().peek() !== prop) {
                        if(sel is IFormSelect && new.isEmpty()) {
                            sel.selectAny()
                        } else if (sel is IFormInput) {
                            sel.input(new)
                        } else {
                            sel.text(new).click()
                        }
                        filledProps.add(prop)
                    }
                }
            }

        fun switch(onTrue: BaseSelector? = null, onFalse: BaseSelector? = null, default: Boolean) =
            Delegates.observable(default) { prop, old, new ->
                propTrig = true
                onTrue?.let {
                    lazyPropMap[prop] = it
                }
                onFalse?.let {
                    lazyPropMap[prop] = it
                }
                if(isInit.get() == true) {
                    if(new) {
                        onTrue?.let {
                            makeVisible(it, prop)
                            if(ignoreInput.get().peek() !== prop) {
                                it.click()
                            }
                        }
                    } else {
                        onFalse?.let {
                            makeVisible(it, prop)
                            if(ignoreInput.get().peek() !== prop) {
                                it.click()
                            }
                        }
                    }
                    filledProps.add(prop)
                }
            }

        fun checkBox(cb: CheckBox, default: Boolean = true) =
            Delegates.observable(default) { prop, _, new ->
                propTrig = true
                lazyPropMap[prop] = cb

                if(isInit.get() == true) {
                    makeVisible(cb, prop)
                    if(ignoreInput.get().peek() !== prop) {
                        if (new) {
                            cb.check()
                        } else {
                            cb.uncheck()
                        }
                        filledProps.add(prop)
                    }
                }
            }

    } val Fields = FieldsCls()

    fun readFromUI(): IBaseModel {
        isInit.set(false)
        lazyPropMap.forEach { prop, sel ->
            if(prop is KMutableProperty<*>) {
                val thiz = findParent(this@IBaseModel, prop)
                if(sel is IFormRead) {
                    when(prop.returnType.javaType.typeName.substringAfterLast(".").lowercase()) {
                        "int" -> prop.setter.call(thiz, sel.readInt())
                        "boolean" -> prop.setter.call(thiz, sel.readBool())
                        else -> prop.setter.call(thiz, sel.readString())
                    }
                } else {
                    if(sel.isVisible) {
                        val v = try {
                            sel.value
                        } catch (e: Exception) {
                            sel.text
                        }

                        try {
                            prop.setter.call(thiz, v)
                        } catch (e: Exception) {
                            Log.error("Can't set value for the: ${prop.name}")
                        }
                    }
                }
            }
        }
        isInit.set(true)
        return  this
    }

    open fun toKV(): Collection<ModelProperty> {
        val res = ArrayList<ModelProperty>()

        mappings.forEach { (prop, sel) ->
            val parent = findParent(this, prop)
            val v = prop.getter.call(parent).toString()
            if(v.isNotEmpty() && prop.annotations.find { it.annotationClass == Model.DataTypes.Ignore::class } == null) {
                res.add(
                    ModelProperty(
                        name = sel.name,
                        value = v,
                        annotations = prop.annotations
                    )
                )
            }
        }

        return res
    }

    companion object {
        const val DEFAULT = 0
        const val CORRECT = 1
        const val INCORRECT = 2
        const val EMPTY = 3

        val isInit = ThreadLocal<Boolean>().apply {
            set(true)
        }

        val ignoreInput = ThreadLocal<java.util.ArrayDeque<KProperty<*>>>().apply {
            set(java.util.ArrayDeque())
        }
    }
}

data class ModelProperty(
    val name: String,
    val value: String,
    val annotations: Collection<Annotation>
)

val KProperty<*>.isPrimitive: Boolean
    get() {
        return this.returnType.toString().endsWith("String") || ClassUtils.isPrimitiveOrWrapper(this.javaClass)
    }

fun <T : Any> T.clone() :T {
    val before = IBaseModel.isInit.get() == true
    IBaseModel.isInit.set(false)

    val res = this.newInstance()
    res.copyProps(this)

    val props = this::class.memberProperties.filter {
        it.returnType.toString().startsWith(
            this.javaClass.name.replace("$",".")
        )
    }

    props.forEach {
        if(it is KMutableProperty<*>) {
           // val cloned = it.getter.call(this)!!.clone()
           // it.setter.call(res, cloned)
        } else {
            val to = it.getter.call(res)!!
            val from = it.getter.call(this)!!
            to.copyProps(from)
        }
    }

    IBaseModel.isInit.set(before)
    return res
  //  return props.keys.associateWith { props[it]?.get(this) }
}

fun Any.copyProps(from: Any) {
    this::class.memberProperties
        .filterIsInstance<KMutableProperty<*>>()
        .forEach {
            val v = it.getter.call(from)!!
            val cls = v.javaClass
            if(cls.name.endsWith("String") || ClassUtils.isPrimitiveOrWrapper(cls)) {
                it.setter.call(this, v)
            }
        }
}