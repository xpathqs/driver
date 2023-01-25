package org.xpathqs.driver.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotSameAs
import org.junit.jupiter.api.Test
import org.xpathqs.gwt.GIVEN
import kotlin.properties.Delegates

data class SimpleCls(
    var str: String = "1",
    var int: Int = 0
)

data class SimpleClsWithInnerClass(
    var str: String = "1",
    var int: Int = 0
) {
    inner class Inner {
        var p1: String = "p1"
        var p2: String = "p2"
    } var in1 = Inner()
}


class ClsWithDelegates {
    var s1: String by Delegates.observable("def1") { prop, old, new ->
    }
    var i1: Int by Delegates.observable(1) { prop, old, new ->
    }
}

class ClsWithMemberDelegates {
    var s1: String by Delegates.observable("def1") { prop, old, new ->
    }
    var i1: Int by Delegates.observable(1) { prop, old, new ->
    }

    var in1 = ClsWithDelegates()
}

class ClsWithInnerDelegates {
    var s1: String by Delegates.observable("def1") { prop, old, new ->
    }
    var i1: Int by Delegates.observable(1) { prop, old, new ->
    }

    inner class ClsWithDelegates {
        var s1: String by Delegates.observable("def1") { prop, old, new ->
        }
        var i1: Int by Delegates.observable(1) { prop, old, new ->
        }
    } var in1 = ClsWithDelegates()
}

class CloneTest {

    @Test
    fun `clone simple class with primitives`() {
        GIVEN {
            SimpleCls("10", 10)
        }.WHEN {
            clone(given)
        }.THEN {
            assertThat(given)
                .isNotSameAs(actual)

            assertThat(given)
                .isEqualTo(actual)

            actual.str = "11"
            assertThat(given)
                .isNotEqualTo(actual)
        }
    }

    @Test
    fun `clone class with inner class object`() {
        GIVEN {
            val given = SimpleClsWithInnerClass("10", 10)
            given.in1.p1 = "inp1"
            given.in1.p2 = "inp2"
            given
        }.WHEN {
            clone(given)
        }.THEN {
            assertThat(given)
                .isNotSameAs(actual)

            assertThat(given.in1)
                .isNotSameAs(actual.in1)

            assertThat(given)
                .isEqualTo(actual)
        }
    }

    @Test
    fun `clone simple class with delegates`() {
        GIVEN {
            val given = ClsWithDelegates()
            given.s1 = "sss"
            given.i1 = 100
            given
        }.WHEN {
            clone(given)
        }.THEN {
            assertThat(given)
                .isNotSameAs(actual)

            assertThat(given.s1)
                .isEqualTo(actual.s1)

            assertThat(given.i1)
                .isEqualTo(actual.i1)

            actual.s1 = "11"
            actual.i1 = 1
            assertThat(given.s1)
                .isNotEqualTo(actual.s1)

            assertThat(given.i1)
                .isNotEqualTo(actual.i1)
        }
    }

    @Test
    fun `clone class with delegates with object member`() {
        GIVEN {
            val given = ClsWithMemberDelegates()
            given.s1 = "sss"
            given.i1 = 100
            given.in1.s1 = "ssss"
            given.in1.i1 = 4444
            given
        }.WHEN {
            clone(given)
        }.THEN {
            assertThat(given)
                .isNotSameAs(actual)

            assertThat(given.in1)
                .isNotSameAs(actual.in1)

            assertThat(given.s1)
                .isEqualTo(actual.s1)

            assertThat(given.i1)
                .isEqualTo(actual.i1)

            actual.s1 = "11"
            actual.i1 = 1
            assertThat(given.s1)
                .isNotEqualTo(actual.s1)

            assertThat(given.i1)
                .isNotEqualTo(actual.i1)
        }
    }

    @Test
    fun `clone class with delegates with inner member`() {
        GIVEN {
            val given = ClsWithInnerDelegates()
            given.s1 = "sss"
            given.i1 = 100
            given.in1.s1 = "ssss"
            given.in1.i1 = 4444
            given
        }.WHEN {
            clone(given)
        }.THEN {
            assertThat(given)
                .isNotSameAs(actual)

            assertThat(given.in1)
                .isNotSameAs(actual.in1)

            assertThat(given.s1)
                .isEqualTo(actual.s1)

            assertThat(given.i1)
                .isEqualTo(actual.i1)

            actual.s1 = "11"
            actual.i1 = 1
            assertThat(given.s1)
                .isNotEqualTo(actual.s1)

            assertThat(given.i1)
                .isNotEqualTo(actual.i1)
        }
    }
}