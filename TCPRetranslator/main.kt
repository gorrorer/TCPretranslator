fun main(args: Array<String>){
    val test = TCPRetranslator()
    test.connectTransmitter("server", "localhost", 2000)
    test.connectReceiver("client", "localhost", 3000)
    test.retranslate()
}