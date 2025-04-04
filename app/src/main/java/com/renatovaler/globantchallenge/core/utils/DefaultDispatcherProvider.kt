package com.renatovaler.globantchallenge.core.utils

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main = Dispatchers.Main
    override val mainImmediate = Dispatchers.Main.immediate
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
}




