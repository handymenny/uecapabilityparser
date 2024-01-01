package it.smartphonecombo.uecapabilityparser

import io.mockk.every
import io.mockk.mockkStatic
import it.smartphonecombo.uecapabilityparser.cli.Clikt
import it.smartphonecombo.uecapabilityparser.server.JavalinApp
import it.smartphonecombo.uecapabilityparser.util.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.jupiter.api.BeforeAll
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2, time=15, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time=10, timeUnit = TimeUnit.SECONDS)
open class Benchmark {
    private val dispatcher = StandardTestDispatcher()

    @Setup(Level.Trial)
    fun setup() {
        Config["reparse"] = "force"
        //Config["compression"] = "true"
        Config["store"] = "store/shuffle"
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns dispatcher
    }

    @Benchmark
    open fun measureName(bh: Blackhole) {
        val app = JavalinApp().app
        dispatcher.scheduler.advanceUntilIdle()
        bh.consume(app)
    }

    @TearDown(Level.Iteration)
    open fun sleep() {
        Thread.sleep(1000)
    }
}
