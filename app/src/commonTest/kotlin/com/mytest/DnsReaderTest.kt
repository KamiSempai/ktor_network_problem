package com.mytest

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class DnsReaderTest {

    @Test
    fun testReadAllDns() {
        runBlocking {
            var hasAnySuccess = false
            ALL_DNS.forEach { dnsHost ->
                try {
                    val result = readDnsRecords(InetSocketAddress(dnsHost, DNS_PORT), REQUEST_DATA)
                    println("Result: [${result.joinToString { it.toString() }}]")
                    hasAnySuccess = true
                } catch (e: Exception) {
                    // Ignore
                }
            }
            if (!hasAnySuccess) {
                throw RuntimeException("Failed to handle any dns servers")
            }
        }
    }

    @Test
    fun testReadDns() {
        runBlocking {
            try {
                val result = readDnsRecords(InetSocketAddress(DNS_HOST, DNS_PORT), REQUEST_DATA)
                println("Result: [${result.joinToString { it.toString() }}]")
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private suspend fun readDnsRecords(
        dnsAddress: InetSocketAddress,
        requestData: ByteArray
    ): ByteArray {
        println()
        println("Reading ${dnsAddress.hostname}:${dnsAddress.port}")

        val selectorManager = SelectorManager(Dispatchers.Default)
        val serverSocket = aSocket(selectorManager).udp().connect(dnsAddress)

        return try {
            serverSocket.send(Datagram(ByteReadPacket(requestData), dnsAddress))
            serverSocket.receive().packet.readBytes()
        } catch (e: Exception) {
            println("Failed to read. Reason: $e")
            throw e
        } finally {
            serverSocket.dispose()
        }
    }


    companion object {
        private val REQUEST_DATA = byteArrayOf(
            0, 0, 1, 16, 0, 1, 0, 0, 0, 0, 0, 1,
            4, 115, 111, 109, 101,
            6, 100, 111, 109, 97, 105, 110,
            3, 99, 111, 109,
            0, 0, 33, 0, 1, 0, 0, 41, 4, 0, 0, 0, -128, 0, 0, 0
        )
        private val ALL_DNS = arrayOf(
            "8.8.8.8", "8.8.4.4", // Google
            "76.76.2.0", "76.76.10.0", // Control D
            "9.9.9.9", "149.112.112.112", // Quad9
            "208.67.222.222", "208.67.220.220", // OpenDNS Home
            "1.1.1.1", "1.0.0.1", // Cloudflare
            "185.228.168.9", "185.228.169.9", // CleanBrowsing
            "76.76.19.19", "76.223.122.150", // Alternate DNS
            "94.140.14.14", "94.140.15.15" // AdGuard DNS
        )

        private const val DNS_PORT = 53
        private val DNS_HOST = ALL_DNS[0]
    }
}