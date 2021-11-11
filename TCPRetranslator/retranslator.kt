import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class TCPRetranslator() {

    lateinit var transmitter: Socket
    lateinit var receiver: Socket


    private fun receiveSortedData(transmitter: Socket): MutableMap<Int, Int> {
        val byteMap = mutableMapOf<Int, Int>()
        val range = 97..122
        for (key in range) {
            byteMap[key] = 0
        }
        val inputStream = transmitter.getInputStream()
        var data = inputStream.read()
        while (data in range){
            byteMap[data] = byteMap[data]!!.inc()
            data = inputStream.read()
        }
        transmitter.close()
        inputStream.close()
        return byteMap
    }

    private fun assembleByteArray(byteMap: MutableMap<Int, Int>): ByteArray {
        val byteList = mutableListOf<Byte>()
        byteMap.forEach {
            if (it.value != 0) {
                for (i in 1..it.value) {
                    byteList.add(it.key.toByte())
                }
            }
        }
        return byteList.toByteArray()
    }

    private fun transmitData(receiver: Socket, data: ByteArray): Boolean {
        val outputStream = receiver.getOutputStream()
        outputStream.write(data)
        outputStream.flush()
        receiver.close()
        outputStream.close()
        return true
    }

    fun connectReceiver(type: String, clientAddress: String, clientPort: Int) {
        receiver = establishConnection(type, clientAddress, clientPort)
    }

    fun connectReceiver(type: String, serverPort: Int) {
        receiver = establishConnection(type, "", serverPort)
    }

    fun connectTransmitter(type: String, clientAddress: String, clientPort: Int) {
        transmitter = establishConnection(type, clientAddress, clientPort)
    }

    fun connectTransmitter(type: String, serverPort: Int) {
        transmitter = establishConnection(type, "", serverPort)
    }

    private fun establishConnection(type: String, address: String, port: Int): Socket {
        when (type) {
            "server" -> {
                return ServerSocket(port).accept()
            }
            "client" -> {
                return Socket(address, port)
            }
        }
        throw SocketException()
    }

    fun retranslate() {
        val data = assembleByteArray(receiveSortedData(transmitter))
        transmitData(receiver, data)
    }

}