import java.net.ServerSocket
import java.net.Socket

class TCPRetranslator(_receiverAddress: String, _receiverPort: Int, _localPort: Int) {

    private val receiverAddress = _receiverAddress
    private val receiverPort = _receiverPort
    private val localPort = _localPort


    private fun receiveSortedData(localport: Int): MutableMap<Int, Int> {
        val byteMap = mutableMapOf<Int, Int>()
        val range = 97..122
        for (key in range) {
            byteMap[key] = 0
        }
        val server = ServerSocket(localport)
        val client = server.accept()
        val inputStream = client.getInputStream()
        var data = inputStream.read()
        while (data in range){
            byteMap[data] = byteMap[data]!!.inc()
            data = inputStream.read()
        }
        client.close()
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

    private fun transmitData(receiverAddress: String, receiverPort: Int, data: ByteArray): Boolean {
        val connection = Socket(receiverAddress, receiverPort)
        val outputStream = connection.getOutputStream()
        outputStream.write(data)
        outputStream.flush()
        connection.close()
        outputStream.close()
        return true
    }

    fun retranslate(): Boolean {
        val sortedMap = receiveSortedData(localPort)
        val data = assembleByteArray(sortedMap)
        transmitData(receiverAddress, receiverPort, data)
        return true
    }

}