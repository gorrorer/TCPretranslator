fun main(args: Array<String>){
    val test = TCPRetranslator("localhost", 3000, 2000)
    test.retranslate()
}