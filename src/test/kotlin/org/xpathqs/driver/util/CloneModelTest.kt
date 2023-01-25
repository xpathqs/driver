package org.xpathqs.driver.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotSameAs
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import org.xpathqs.core.reflection.PackageScanner
import org.xpathqs.core.util.SelectorFactory.tagSelector
import org.xpathqs.driver.model.IBaseModel
import org.xpathqs.driver.model.clone
import org.xpathqs.driver.navigation.base.IModelBlock
import org.xpathqs.driver.page.Page

import org.xpathqs.gwt.GIVEN
import kotlin.properties.Delegates

object DefaultModels {
    val defaultPageModel = Page1.PageModel().apply {
        s1 = "test"
    }

    val defaultInnerPageModel = InnerPage1.PageModel().apply {
        in1.s1 = "test"
    }

    val defaultInheritancePageModel = InheritancePage.PageModel().apply {
        setInner()
    }
}

object Page1: Page(), IModelBlock<Page1.PageModel> {
    val s1 = tagSelector("div")

    class PageModel: IBaseModel(Page1) {
        var s1 by Fields.input()
    }

    override fun invoke() = DefaultModels.defaultPageModel.clone()
}

object InnerPage1: Page(), IModelBlock<InnerPage1.PageModel> {
    val s1 = tagSelector("div")

    class PageModel: IBaseModel(InnerPage1) {
        inner class Inner {
            var s1 by Fields.input(InnerPage1.s1)
        } var in1 = Inner()
    }

    override fun invoke() = DefaultModels.defaultInnerPageModel.clone()
}

object InheritancePage: Page(), IModelBlock<InheritancePage.PageModel> {
    val s1 = tagSelector("div")
    val s2 = tagSelector("div")

    class PageModel: IBaseModel(InheritancePage) {
        open inner class Inner {
            var s1 by Fields.input(InheritancePage.s1)
        }
        inner class Child: Inner() {
            var s2 by Fields.input(InheritancePage.s2)
        }
        var in1: Inner = Inner()

        fun setInner() {
            in1 = Child()
            val m = in1 as Child
            m.s1 = "aaaa"
            m.s2 = "bbbb"
        }
    }

    override fun invoke() = DefaultModels.defaultInheritancePageModel.clone()
}


class CloneModelTest {
    init {
        PackageScanner("org.xpathqs.driver.util")
            .scan()

        IBaseModel.disableUiUpdate()
    }

    @Test
    fun `clone model with one field`() {
        GIVEN {
            DefaultModels.defaultPageModel
        }.WHEN {
            Page1()
        }.THEN {
            assertThat(actual)
                .isNotSameAs(given)

            assertThat(actual.s1)
                .isEqualTo(given.s1)

            given.s1 = "t1"

            assertThat(actual.s1)
                .isNotEqualTo(given.s1)
        }
    }


    @Test
    fun `clone model with inner class field`() {
        GIVEN {
            DefaultModels.defaultInnerPageModel
        }.WHEN {
            InnerPage1()
        }.THEN {
            assertThat(actual)
                .isNotSameAs(given)

            assertThat(actual.in1)
                .isNotSameAs(given.in1)

            assertThat(actual.in1.s1)
                .isEqualTo(given.in1.s1)

            given.in1.s1 = "t1"



            assertThat(actual.in1.s1)
                .isNotEqualTo(given.in1.s1)

            assertThat(actual.findSelByProp(InnerPage1.PageModel.Inner::s1))
                .isSameAs(given.findSelByProp(InnerPage1.PageModel.Inner::s1))
        }
    }

    @Test
    fun `clone model with inheritance member`() {
        GIVEN {
            DefaultModels.defaultInheritancePageModel
        }.WHEN {
            InheritancePage()
        }.THEN {
            val actualObj = actual.in1 as InheritancePage.PageModel.Child
            val givenObj = given.in1 as InheritancePage.PageModel.Child

            assertThat(actual)
                .isNotSameAs(given)

            assertThat(actual)
                .isNotSameAs(given.in1)

            assertThat(actual.in1.javaClass)
                .isSameAs(given.in1.javaClass)

            assertThat(actualObj.s1)
                .isEqualTo(givenObj.s1)
            assertThat(actualObj.s2)
                .isEqualTo(givenObj.s2)

            given.in1.s1 = "t1"

            assertThat(actual.in1.s1)
                .isNotEqualTo(given.in1.s1)

            assertThat(actual.findSelByProp(InheritancePage.PageModel.Inner::s1))
                .isSameAs(given.findSelByProp(InheritancePage.PageModel.Inner::s1))

            assertThat(actual.findSelByProp(InheritancePage.PageModel.Child::s2))
                .isSameAs(given.findSelByProp(InheritancePage.PageModel.Child::s2))
        }
    }
}