package it.smartphonecombo.uecapabilityparser.server

object ServerMode {

    /**
     * Start a server listening to [port], if [port] is 0 a random port is used. Return the port
     * used by the server (useful for input 0)
     */
    fun run(port: Int): Int {
        val app = JavalinApp().newServer()
        app.start(port)
        Runtime.getRuntime().addShutdownHook(Thread { app.stop() })
        return app.port()
    }
}
