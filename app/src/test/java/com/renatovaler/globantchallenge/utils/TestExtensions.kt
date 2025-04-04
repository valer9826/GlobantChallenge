package com.renatovaler.globantchallenge.utils

import com.google.common.truth.Truth.assertThat

inline fun <reified T : Throwable> Result<*>.assertFailureOfType() {
    assertThat(this.isFailure).isTrue()
    assertThat(this.exceptionOrNull()).isInstanceOf(T::class.java)
}
