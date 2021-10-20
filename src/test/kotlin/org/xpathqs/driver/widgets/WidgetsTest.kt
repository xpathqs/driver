package org.xpathqs.driver.widgets

import org.junit.jupiter.api.Test
import org.xpathqs.core.reflection.scanPackage
import org.xpathqs.core.selector.base.BaseSelector
import org.xpathqs.core.selector.block.Block
import org.xpathqs.core.util.SelectorFactory.tagSelector
import org.xpathqs.core.util.SelectorFactory.textSelector
import org.xpathqs.driver.navigation.annotations.UI
import org.xpathqs.driver.page.Page
import org.xpathqs.gwt.WHEN
import kotlin.reflect.KProperty

class LoginModel() : IBaseModel() {

    var password: String by Fields.input()
    var userName: String by Fields.input()

    override val mappings: Map<KProperty<*>, BaseSelector>
        get() =
            mapOf(
                ::userName to AuthPage.login,
                ::password to AuthPage.password
            )
}

@UI.Widgets.Form(LoginModel::class)
object AuthPage : Page() {
    val login = tagSelector("input1")
    val password = tagSelector("input2")

    @UI.Widgets.Submit
    val submit = tagSelector("Submit")

    @UI.Visibility.Dynamic
    object Error : Block() {
        val errorMsg = textSelector(text = "Invalid creds")
    }
}

class WidgetsTest {
    init {
        scanPackage(this)
    }

    @Test
    fun containers() {
        WHEN {
            LoginModel().containers.first()
        }.THEN {
            AuthPage
        }
    }

    @Test
    fun findWidget() {
        WHEN {
            LoginModel().findWidget(UI.Widgets.Submit::class)
        }.THEN {
            AuthPage.submit
        }
    }
}