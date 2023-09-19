package com.example

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test;

@Suppress("FunctionName")
class HelloTest {

    @Test
    fun `hello world`() {
        assertThat(1).isEqualTo(1)
    }
}
