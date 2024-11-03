package it.smartphonecombo.uecapabilityparser.io

import it.smartphonecombo.uecapabilityparser.util.Config
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO

private val customDispatcher = IO.limitedParallelism(Config.getOrDefault("maxThreads", "2").toInt())

val Dispatchers.Custom: CoroutineDispatcher
    get() = customDispatcher
