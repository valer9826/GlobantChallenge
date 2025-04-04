package com.renatovaler.globantchallenge.utils

import com.renatovaler.globantchallenge.core.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatcherProvider @OptIn(ExperimentalCoroutinesApi::class) constructor(
    internal val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main = testDispatcher
    override val mainImmediate = testDispatcher
    override val io = testDispatcher
    override val default = testDispatcher
}


