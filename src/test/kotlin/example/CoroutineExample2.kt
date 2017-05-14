package example

import kotlinx.coroutines.experimental.runBlocking

// testing several calls
fun main(args: Array<String>) = runBlocking<Unit> {
    println("Started!")
    test()
    foo()
    test()
    println("Done.")
}

suspend fun foo() {
    println("hi")
    test()
}