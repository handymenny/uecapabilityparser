package it.smartphonecombo.uecapabilityparser.server

object ServerMode {
    fun run(port: Int) {
        val app = JavalinApp().app
        app.start(port)
        Runtime.getRuntime().addShutdownHook(Thread { app.stop() })
    }
}
