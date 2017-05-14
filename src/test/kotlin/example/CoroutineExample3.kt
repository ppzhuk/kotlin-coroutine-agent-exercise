package example

import kotlinx.coroutines.experimental.runBlocking

// different classes
fun main(args: Array<String>) = runBlocking<Unit> {
    println("Started!")
    A().foo()
    B.boo(10)
    println("Done.")
}


private class A() {
    suspend fun foo() {
        println("foo")
        test()
    }
}

private class B() {
    // a little bit more complicated logic
    companion object {
        suspend fun boo(i: Int) {
            println("boo")
            try {
                if (i > 0) {
                    test()
                    throw Exception()
                }
            } catch (e: Exception) {
                test()
            }
        }
    }
}