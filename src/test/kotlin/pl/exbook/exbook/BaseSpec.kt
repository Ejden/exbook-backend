package pl.exbook.exbook

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

abstract class BaseSpec {

    @Test
    fun `1 == 1`() {
        // expect
        assertThat(1).isEqualTo(1)
    }
}
