package ai.magicdb.dataservice.core.script

import java.io.OutputStream
import java.io.PrintStream
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 日志收集输出流
 *
 * @author magicdb
 */
class LoggingPrintStream(
    private val console: CopyOnWriteArrayList<String>,
    private val originalStream: PrintStream
) : PrintStream(originalStream as OutputStream) {

    override fun println(x: Any?) {
        val message = x?.toString() ?: "null"
        console.add(message)
        originalStream.println(x)
    }

    override fun println(x: String?) {
        val message = x ?: "null"
        console.add(message)
        originalStream.println(x)
    }

    override fun println(x: Int) {
        console.add(x.toString())
        originalStream.println(x)
    }

    override fun println(x: Long) {
        console.add(x.toString())
        originalStream.println(x)
    }

    override fun println(x: Float) {
        console.add(x.toString())
        originalStream.println(x)
    }

    override fun println(x: Double) {
        console.add(x.toString())
        originalStream.println(x)
    }

    override fun println(x: Boolean) {
        console.add(x.toString())
        originalStream.println(x)
    }

    override fun println(x: Char) {
        console.add(x.toString())
        originalStream.println(x)
    }

    override fun println() {
        console.add("")
        originalStream.println()
    }

    override fun print(x: Any?) {
        val message = x?.toString() ?: "null"
        console.add(message)
        originalStream.print(x)
    }

    override fun print(x: String?) {
        val message = x ?: "null"
        console.add(message)
        originalStream.print(x)
    }

    override fun print(x: Int) {
        console.add(x.toString())
        originalStream.print(x)
    }

    override fun print(x: Long) {
        console.add(x.toString())
        originalStream.print(x)
    }

    override fun print(x: Float) {
        console.add(x.toString())
        originalStream.print(x)
    }

    override fun print(x: Double) {
        console.add(x.toString())
        originalStream.print(x)
    }

    override fun print(x: Boolean) {
        console.add(x.toString())
        originalStream.print(x)
    }

    override fun print(x: Char) {
        console.add(x.toString())
        originalStream.print(x)
    }
}
