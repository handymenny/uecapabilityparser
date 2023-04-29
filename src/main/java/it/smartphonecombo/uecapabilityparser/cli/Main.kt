package it.smartphonecombo.uecapabilityparser.cli

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.UsageError

internal object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        // If args contains server, use Clikt.parse to avoid System.exit() for exceptions
        if (args.contains("-s") || args.contains("--server")) {
            try {
                Clikt.parse(args)
            } catch (e: PrintMessage) {
                println(e.message)
            } catch (e: UsageError) {
                System.err.println(e.helpMessage())
            } catch (e: CliktError) {
                System.err.println(e.message)
            }
        } else {
            Clikt.main(args)
        }
    }
}
