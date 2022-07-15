package org.xpathqs.driver.model

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
import org.xpathqs.driver.util.clone
import org.xpathqs.driver.widgets.*
import java.time.Duration
import kotlin.properties.Delegates
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmName


open class IBaseModel(
    val view: Block? = null,
    val comporator: IModelComporator = DefaultComparator()
) {
    private var mappingsInitialized = false

    val mappingsDelegate = resetableLazy {
        val res = LinkedHashMap<KProperty<*>, BaseSelector>()
        res.putAll(reflectionMappings)
        res.putAll(propArgsMappings)

        mappingsInitialized = true
        res
    }

    open val mappings: LinkedHashMap<KProperty<*>, BaseSelector>
        by mappingsDelegate

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
            ignoreUpdateModel {
                allProperties().forEach { p ->
                    val parent = findParent(this, p)
                    try {
                        val v = p.getter.call(parent)
                        (p as? KMutableProperty<*>)?.setter?.call(parent, v)
                    } catch (e: Exception) {
                        Log.error("No value for the '${p.name}'\n${e.message}")
                    }
                }
            }

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

   /* fun<T> updateModel(l: IBaseModel.()->T): T {
        disableUiUpdate()
        val r = this.l()
        enableUiUpdate()
        return r
    }*/

    fun<T> ignoreUpdateModel(l: IBaseModel.()->T): T {
        val before = isInit.get() == true
        isInit.set(false)

        val r = this.l()

        isInit.set(before)
        return r
    }

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
        if(p != null) {
            val pathTo = p.findAnnotation<UI.Nav.PathTo>()?.bySubmit?.objectInstance
            if(pathTo is ILoadable) {
                pathTo.waitForLoad(Duration.ofSeconds(30))
            }
        }
    }

    open fun reset() {
        mappings.keys.forEach {
            if(it.getter.returnType.javaType.typeName.endsWith("String")) {
                val s = findSelByProp(it)
                makeVisible(s, it, true)
                if(s is IFormInput) {
                    s.clear()
                } else {
                    (it as KMutableProperty<*>).setter.call(this, "")
                }
                val block = s.rootParent
                if(block is Block) {
                    block.findWithAnnotation(UI.Widgets.ClickToClose::class)?.let {
                        it.click()
                        Thread.sleep(100)
                    }
                }

            }
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
                        //     throw e
                    }catch (e: Error) {
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

    private var checkFilled = false

    open fun fill(prop: KMutableProperty<*>, other: IBaseModel? = null) {
        Log.action("fill the ${prop.name}") {
            val parent = findParent(this, prop)

            val v = try {
                prop.getter.call(parent)
            } catch (e: Exception) {
                Log.error("Can't get prop value")
                throw e
            }

            if(other != null) {
                val otherParent = findParent(other, prop)
                val v2 = try {
                    prop.getter.call(otherParent)
                } catch (e: Exception) {
                    Log.error("Can't get prop value")
                    throw e
                }

                if(comporator.isEqual(prop, v, v2)) {
                    return@action
                }
            }

            //When property is not a delegate
            if(prop.isPrimitive && v != null && !lazyPropMap.containsKey(prop)) {
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
                Log.action("Set $v to the ${parent?.toString()}") {
                    prop.setter.call(parent, v)
                }
            }
        }
    }

    open fun fill(noSubmit: Boolean = false, other: IBaseModel? = null) {
        filledProps.clear()
        checkFilled = true
        if(this is IOrderedSteps) {
            evalActions(steps, noSubmit, other)
        } else {
            this.mappings.keys.forEach {
                fill(it as KMutableProperty<*>, other)
            }
        }
        checkFilled = false
        this.propTrig = true
    }

    private fun evalActions(steps: Collection<InputAction>, noSubmit: Boolean = false, other: IBaseModel? = null) {
        checkFilled = true
        steps.forEach {
            val action = if(it is SwitchInputAction) {
                if (it.func()) it.onTrue else it.onFalse
            } else {
                it
            }

            action.props.forEach {
                if(it.getter.parameters.isEmpty()) {
                    val v = it.getter.call()
                    (it as KMutableProperty<*>).setter.call(v)
                } else {
                    val parent = findParent(this, it)
                    try {
                        val v = it.getter.call(parent)

                        if(other != null) {
                            val parentOther = findParent(other, it)
                            val v2 = it.getter.call(parentOther)
                            if(!comporator.isEqual(it, v, v2)) {
                                (it as KMutableProperty<*>).setter.call(parent, v)
                            }
                        } else {
                            (it as KMutableProperty<*>).setter.call(parent, v)
                        }
                    } catch (e: Exception) {
                        val v = it.getter.call(parent)
                        (it as KMutableProperty<*>).setter.call(parent, v)
                    }
                }
            }

            Thread.sleep(500) //short delay after input action
            if(action.type == InputType.SUBMIT && !noSubmit) {
                Thread.sleep(500)
                val sel =
                    if(action.props.isEmpty()) {
                        getSelector(steps.first())
                    } else {
                        getSelector(action)
                    }

                (sel.rootParent as Block).findWithAnnotation(
                    UI.Widgets.Submit::class
                )?.waitForVisible()?.click() ?: throw Exception("No Submit Widget button")

                submitCalled = true
            }
        }
        checkFilled = false
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
            states[state]?.submit()
        }
    }

    open fun submit(page: INavigable) {
        if(this is IOrderedSteps) {
            val stepsToSubmit = ArrayList<InputAction>()
            steps.forEach { action ->
                if(action.type == InputType.DYNAMIC
                    || (action.type == InputType.SUBMIT && action.props.isEmpty())
                ) {
                    stepsToSubmit.add(action)
                } else {
                    val sel = getSelector(action)
                    page as BaseSelector
                    if(sel.parents.find { it.name == page.name} != null) {
                        stepsToSubmit.add(action)
                    }
                }
            }
            evalActions(stepsToSubmit, other = modelFromUi)
        } else {
            if(states.containsKey(CORRECT)) {
                submit(CORRECT)
            } else {
                submit()
            }
        }
    }

    fun findSelByProp(prop: KProperty<*>) =
        mappings.filterKeys {
            it.name == prop.name
        }.values.first()

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

    val KProperty<*>.fullName: String
        get() {
            return this.toString().substringAfter("var ").substringBefore(":")
        }

    fun findParent(source: Any, prop: KProperty<*>): Any? {
        return Log.action("Finding parent for ${prop.name}, source: $source") {
            try {
                properties(source).forEach {
                    if(it.fullName == prop.fullName || it === prop ) {
                        Log.info("parent was found")
                        return@action source
                    } else {
                        try {
                            val itObj = it.getter.call(source)
                            val sourceObj =  prop.getter.call(source)
                            if((itObj === sourceObj) && itObj != null) {
                                Log.info("parent was found via evaluation")
                                return@action source
                            }
                        } catch (e: Exception) {

                        }
                    }
                    if(!it.isPrimitive) {
                        try {
                            it.isAccessible = true
                        } catch (e:  Error) {

                        } catch (e:  Exception) {

                        }
                        var res: Any? = null
                        try {
                            Log.info("parent will be checked in recursion")
                            res = findParent(it.getter.call(source)!!, prop)
                        } catch (e:  Exception) {
                            Log.error("Exception in find parent")
                        } catch (e: Error) {
                            Log.error("Error in find parent")
                        }
                        if(res != null) {
                            return@action res
                        }
                    }
                }
            } catch (e: Exception) {
                Log.error("Exception in evaluation")
            }

            return@action null
        }
    }

    private fun properties(obj: Any = this) = obj::class.memberProperties.filter {
        it is KMutableProperty<*>
                || it.returnType.javaType.typeName.startsWith(obj::class.jvmName)
    }

    private fun allProperties(obj: Any = this): Collection<KProperty<*>> {
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

        if (res.isEmpty() && obj::class.declaredMemberProperties.isNotEmpty()) {
            throw XPathQsException.ModelDoesntHaveMutableProps(this)
        }

        val c = this::class.java.declaredFields
        val orderById = c.withIndex().associate { it.value.name to it.index }

        return res.sortedBy { orderById[it.name] }
    }

    fun makeVisible(sel: BaseSelector, prop: KProperty<*>, disabled: Boolean = false) {
        Log.action("Trying to make visible ${sel.name}") {
            val p = findParent(this@IBaseModel, prop)

            if(sel.isHidden || disabled) {
                Log.action("Selector $sel is hidden") {
                    if(p is IValueDependency) {
                        val vd = p.valueDependency.find { vd ->
                            vd.source.find { it.name == prop.name } != null
                        }

                        val prop = vd?.dependsOn as? KMutableProperty<*>
                        if(prop != null) {
                            Log.action("Dependency was found of: ${prop.name}") {
                                if(prop.getter.parameters.size == 1) {
                                    Log.info("getter has 1 param")
                                    val thiz = findParent(this@IBaseModel, prop)
                                    if(vd!!.value is DefaultValue) {
                                        val v = prop.getter.call(thiz)
                                        Log.action("Default value(${v.toString()}) will be set") {
                                            prop.setter.call(thiz, v)
                                        }
                                    } else {
                                        Log.action("value(${vd.value}) will be set") {
                                            prop.setter.call(thiz, vd!!.value)
                                        }
                                    }
                                    //     Thread.sleep(500)
                                    vd.source.forEach {
                                        findSelByProp(it).waitForVisible()
                                    }
                                } else {
                                    Log.info("getter has >1 params")
                                    if(vd!!.value is DefaultValue) {
                                        val v = prop.getter.call()
                                        Log.action("Default value(${v.toString()}) will be set") {
                                            prop.setter.call(v)
                                        }
                                    } else {
                                        Log.action("value(${vd.value}) will be set") {
                                            prop.setter.call(vd!!.value)
                                        }
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
                                                    Log.action("Result will be evolved") {
                                                        evalActions(
                                                            listOf(action)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if(this@IBaseModel is IValueDependency) {
                            Log.info("model has a dependency")
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
                Log.info("Selector is still hidden. Lets make it visible via navigation")
                sel.makeVisible()
            }
        }
    }



    inner class FieldsCls {
        fun input(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) { prop, old, new ->
                addMapping(prop, mapping)

                if(isReadyForUiInput()) {
                    Log.action("Input value to the ${mapping?.name} throw input") {
                        if(filledProps.contains(prop) && checkFilled) {
                            return@action
                        }

                        val sel = mappings.filterKeys { it.name == prop.name }.values.first()

                        makeVisible(sel, prop)

                        if(sel is IFormInput) {
                            val s = (sel as Block).findWithAnnotation(UI.Widgets.Input::class)
                            if(s != null) {
                                try {
                                    val disabled = s.getAllAttrs().find {
                                        it.first.equals("disabled", true)
                                    } != null
                                    makeVisible(sel, prop, disabled)
                                } catch (e: Exception) {
                                }
                            }
                        }

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
                }
            }

        fun nothing(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) {
                    prop, old, new ->
                addMapping(prop, mapping)

                if(isReadyForUiInput()) {
                    val sel = mappings.filterKeys { it.name == prop.name }.values.first()
                    makeVisible(sel, prop)
                    filledProps.add(prop)
                }
            }

        private fun addMapping(prop: KProperty<*>, sel: BaseSelector?) {
            propTrig = true
            sel?.let {
                if(mappingsInitialized) {
                    if(!lazyPropMap.containsKey(prop)) {
                        lazyPropMap[prop] = it
                        mappingsDelegate.reset()
                    }
                } else {
                    lazyPropMap[prop] = it
                }
            }
        }

        fun click(mapping: BaseSelector? = null, default: String = "") =
            Delegates.observable(default) { prop, old, new ->
                addMapping(prop, mapping)
                if(isReadyForUiInput()) {
                    Log.action("Input value to the ${mapping?.name} throw click") {
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
            }

        fun switch(onTrue: BaseSelector? = null, onFalse: BaseSelector? = null, default: Boolean) =
            Delegates.observable(default) { prop, old, new ->
                addMapping(prop, onTrue)
                addMapping(prop, onFalse)

                if(isReadyForUiInput()) {
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
                addMapping(prop, cb)

                if(isReadyForUiInput()) {
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

    fun getValueByProp(prop: KProperty<*>): String {
        val parent = findParent(this@IBaseModel, prop)
        return prop.getter.call(parent).toString()
    }

    fun setValueByProp(prop: KMutableProperty<*>, value: Any?) {
        val parent = findParent(this@IBaseModel, prop)
        try {
            prop.setter.call(parent, value)
        }catch (e: Exception) {
            Thread.sleep(300)
            prop.setter.call(parent, value)
        }
    }

    fun readFromUI(): IBaseModel {
        ignoreUpdateModel {
            mappings.forEach { prop, sel ->
                if(sel.isVisible) {
                    if(prop is KMutableProperty<*>) {
                        val thiz = findParent(this@IBaseModel, prop)
                        if(sel is IFormRead) {
                            if(sel.isReady()) {
                                when(prop.returnType.javaType.typeName.substringAfterLast(".").lowercase()) {
                                    "int" -> prop.setter.call(thiz, sel.readInt())
                                    "boolean" -> prop.setter.call(thiz, sel.readBool())
                                    else -> prop.setter.call(thiz, sel.readString())
                                }
                            }
                        } else {
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
        }

        return  this
    }



    open fun toKV(): Collection<ModelProperty> {
        val res = ArrayList<ModelProperty>()

        mappings.forEach { (prop, sel) ->
            val parent = findParent(this, prop)
            try {
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
            } catch (e: Exception) {}

        }

        return res
    }

    companion object {
        const val DEFAULT = 0
        const val CORRECT = 1
        const val INCORRECT = 2
        const val EMPTY = 3

        fun isReadyForUiInput()
                = isInit.get()

        fun disableUiUpdate() {
            isInit.set(false)
        }

        fun enableUiUpdate() {
            isInit.set(true)
        }

        private val isInit = ThreadLocal<Boolean>().apply {
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

fun <T : IBaseModel> T.clone() :T {
    return this.ignoreUpdateModel {
        clone(this) as T
    }
}

val <T : IBaseModel> T.modelFromUi :T
    get() {
        return this.ignoreUpdateModel {
            val model = if(this.view is IModelBlock<*>) (this.view as IModelBlock<*>).getFromUi() else this
            model.readFromUI()
            clone(model) as T
        }
    }




