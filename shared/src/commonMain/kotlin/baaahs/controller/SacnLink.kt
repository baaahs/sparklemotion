package baaahs.controller

import baaahs.Color
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Stats
import baaahs.util.isBefore
import kotlinx.datetime.Instant
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

class SacnLink(
    link: Network.Link,
    private val senderCid: ByteArray,
    sourceName: String,
    private val clock: Clock
) {
    var lastError: Exception? = null
    var lastErrorAt: Instant? = null

    init {
        if (senderCid.size != 16) error("Nope! Sender CID nees to be 16 bytes.")
    }

    val udpSocket = link.listenUdp(0, object : Network.UdpListener {
        override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
            logger.warn { "Received UDP packet from $fromAddress:$fromPort ${bytes.size} bytes" }
        }
    })


    fun deviceAt(address: Network.Address): SacnNode = SacnNode(address)

    inner class SacnNode(private val address: Network.Address) {
        val stats = SacnStats()
        private var sequenceNumber = 0

        fun sendDataPacket(colors: Iterable<Color>) {
            stats.sendDataPacket.time {
                val buf = ByteArrayWriter(512)
                for (color in colors) {
                    buf.writeByte(color.redB)
                    buf.writeByte(color.greenB)
                    buf.writeByte(color.blueB)
                }

                this@SacnLink.sendDataPacket(address, sequenceNumber++, 0x1, buf.toBytes())
            }
        }

        fun sendDataPacket(
            data: ByteArray, universe: Int = 1, dataOffset: Int = 0, dataLength: Int = -1, sequenceNumber: Int = -1
        ) {
            val seqNum = if (sequenceNumber == -1) this.sequenceNumber++ else sequenceNumber

            val now = clock.now()
            if (lastErrorAt?.isBefore(now - waitPeriodAfterNetworkError) != false) {
                try {
                    this@SacnLink.sendDataPacket(address, seqNum, universe, data, dataOffset, dataLength)
                } catch (e: Exception) {
                    logger.error { "Failed to send packet to ${address.asString()}: ${e.message}" }
                    lastError = e
                    lastErrorAt = now
                }
            }
        }
    }

    class SacnStats : Stats() {
        val sendDataPacket by statistic
    }

    private val sourceNameBytes = sourceName.let {
        val value = it.encodeToByteArray()
        ByteArray(64).also { b -> value.copyInto(b) }
    }

    private fun sendDataPacket(
        address: Network.Address,
        sequenceNumber: Int,
        universe: Int,
        dataBytes: ByteArray,
        dataOffset: Int = 0,
        dataLength: Int = -1
    ) {
        val length = if (dataLength == -1) min(dataBytes.size, 512) else dataLength
        if (length > 512) error("Too many DMX channels ($dataBytes > 512).")

        val bytes = ByteArrayWriter().apply {
            // Root Layer
            writeShort(0x10) // Preamble size
            writeShort(0x0) // Post-amble size
            // ACN packet identifier:
            writeBytes(0x41, 0x53, 0x43, 0x2d, 0x45, 0x31, 0x2e, 0x31, 0x37, 0x00, 0x00, 0x00)

            val rootLayerPdu = offset
            writeShort(0) // PDU length
            writeInt(VECTOR_ROOT_E131_DATA) // Vector
            writeBytes(senderCid) // Sender's CID

            // Framing Layer
            val framingLayerPdu = offset
            writeShort(0) // PDU length
            writeInt(VECTOR_E131_DATA_PACKET) // Vector
            writeBytes(sourceNameBytes)
            writeByte(199.toByte()) // Priority
            writeShort(0x0) // Synchronization address
            writeByte((sequenceNumber and 0xFF).toByte()) // Sequence number
            writeByte(0x0) // Options
            writeShort(universe) // Universe

            // DMP Layer
            val dmpLayerPdu = offset
            writeShort(0) // PDU length
            writeByte(VECTOR_DMP_SET_PROPERTY.toByte()) // Vector
            writeByte(0xA1.toByte()) // Address type & data type
            writeShort(0x0) // First property address
            writeShort(0x1) // Address increment
            writeShort(1 + length) // Property value count
            // Property values...
            writeByte(0x0) // START code
            for (i in dataOffset until dataOffset + length)
                writeByte(dataBytes[i])

            at(rootLayerPdu).writeShort(0x7000 or offset - rootLayerPdu)
            at(framingLayerPdu).writeShort(0x7000 or offset - framingLayerPdu)
            at(dmpLayerPdu).writeShort(0x7000 or offset - dmpLayerPdu)
        }.toBytes()

        udpSocket.sendUdp(address, sAcnPort, bytes)
    }

    class DataFrame(
        val universe: Int,
        val channels: ByteArray
    )

    fun sendUniverseDiscoveryPacket() {
        logger.warn { "Sending Universe Discovery Packet" }
        val bytes = ByteArrayWriter().apply {
            // Root Layer
            writeShort(0x10) // Preamble size
            writeShort(0x0) // Post-amble size
            // ACN packet identifier:
            writeBytes(0x41, 0x53, 0x43, 0x2d, 0x45, 0x31, 0x2e, 0x31, 0x37, 0x00, 0x00, 0x00)

            val rootLayerPdu = offset
            writeShort(0) // PDU length
            writeInt(VECTOR_ROOT_E131_EXTENDED) // Vector
            writeBytes(senderCid) // Sender's CID

            // Framing Layer
            val framingLayerPdu = offset
            writeShort(0) // PDU length
            writeInt(VECTOR_E131_EXTENDED_DISCOVERY) // Vector
            writeBytes(sourceNameBytes)
            writeInt(0x0) // Reserved

            // Universe Discovery Layer
            val universeDiscoveryLayerPdu = offset
            writeShort(0) // PDU length
            writeInt(VECTOR_UNIVERSE_DISCOVERY_UNIVERSE_LIST) // Vector
            writeByte(0x0) // Page
            writeByte(0x0) // Last
            // List of universes...
            writeShort(0x1)

            at(rootLayerPdu).writeShort(0x7000 or offset - rootLayerPdu)
            at(framingLayerPdu).writeShort(0x7000 or offset - framingLayerPdu)
            at(universeDiscoveryLayerPdu).writeShort(0x7000 or offset - universeDiscoveryLayerPdu)
        }.toBytes()

        udpSocket.broadcastUdp(sAcnPort, bytes)
    }

    companion object {
        const val sAcnPort = 5568
        val waitPeriodAfterNetworkError = 5.seconds

        const val VECTOR_ROOT_E131_DATA = 0x4
        private const val VECTOR_ROOT_E131_EXTENDED = 0x8

        private const val VECTOR_DMP_SET_PROPERTY = 0x2

        private const val VECTOR_E131_DATA_PACKET = 0x2

        private const val VECTOR_E131_EXTENDED_SYNCHRONIZATION = 0x1
        private const val VECTOR_E131_EXTENDED_DISCOVERY = 0x2
        private const val VECTOR_UNIVERSE_DISCOVERY_UNIVERSE_LIST = 0x1

        fun readDataFrame(byteArray: ByteArray): DataFrame {
            ByteArrayReader(byteArray).apply {
                // Root Layer
                readShort(/*0x10*/) // Preamble size
                readShort(/*0x0*/) // Post-amble size
                // ACN packet identifier:
                readBytes(12 /*0x41, 0x53, 0x43, 0x2d, 0x45, 0x31, 0x2e, 0x31, 0x37, 0x00, 0x00, 0x00*/)

//            val rootLayerPdu = offset
                readShort(/*0*/) // PDU length
                readInt(/*VECTOR_ROOT_E131_DATA*/) // Vector
                readBytes(16 /*senderCid*/) // Sender's CID

                // Framing Layer
//            val framingLayerPdu = offset
                readShort(/*0*/) // PDU length
                readInt(/*VECTOR_E131_DATA_PACKET*/) // Vector
                readBytes(64)
                readByte(/*199.toByte()*/) // Priority
                readShort(/*0x0*/) // Synchronization address
                readByte(/*sequenceNumber.toByte()*/) // Sequence number
                readByte(/*0x0*/) // Options
                val universe = readShort(/*0x1*/).toInt() // Universe

                // DMP Layer
//            val dmpLayerPdu = offset
                readShort(/*0*/) // PDU length
                readByte(/*VECTOR_DMP_SET_PROPERTY.toByte()*/) // Vector
                readByte(/*0xA1.toByte()*/) // Address type & data type
                readShort(/*0x0*/) // First property address
                readShort(/*0x1*/) // Address increment
                val channelCount = readShort() - 1// (/*1 + colors.pixelCount * 3*/) // Property value count
                // Property values...
                readByte(/*0x0*/) // START code
                val channels = ByteArray(channelCount)
                for (i in 0 until channelCount) {
                    channels[i] = readByte()
                }

//            at(rootLayerPdu).writeShort(0x7000 or offset - rootLayerPdu)
//            at(framingLayerPdu).writeShort(0x7000 or offset - framingLayerPdu)
//            at(dmpLayerPdu).writeShort(0x7000 or offset - dmpLayerPdu)

                return DataFrame(universe, channels)
            }
        }

//        E131_E131_UNIVERSE_DISCOVERY_INTERVAL = 10 seconds
//        E131_NETWORK_DATA_LOSS_TIMEOUT = 2.5 seconds
//        E131_DISCOVERY_UNIVERSE = 64214
//        ACN_SDT_MULTICAST_PORT = 5568

        private val logger = Logger<SacnLink>()
    }
}