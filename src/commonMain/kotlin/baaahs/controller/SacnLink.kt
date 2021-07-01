package baaahs.controller

import baaahs.Color
import baaahs.Pixels
import baaahs.fixtures.ColorResultType
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import baaahs.util.Logger
import baaahs.util.Stats

class SacnLink(
    link: Network.Link,
    private val senderCid: ByteArray,
    sourceName: String
) {
    init {
        if (senderCid.size != 16) error("Nope! Sender CID nees to be 16 bytes.")
    }

    val udpSocket = link.listenUdp(0, object : Network.UdpListener {
        override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
            logger.warn { "Received UDP packet from $fromAddress:$fromPort ${bytes.size} bytes" }
        }
    })


    fun deviceAt(address: Network.Address): SacnDevice = SacnDevice(address)

    inner class SacnDevice(private val address: Network.Address) {
        val stats = SacnStats()
        private var sequenceNumber = 0

        fun sendDataPacket(colors: ColorResultType.ColorResultView) {
            stats.sendDataPacket.time {
                this@SacnLink.sendDataPacket(address, sequenceNumber++, colors)
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
        colors: ColorResultType.ColorResultView
    ) {
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
            writeByte(sequenceNumber.toByte()) // Sequence number
            writeByte(0x0) // Options
            writeShort(0x1) // Universe

            // DMP Layer
            val dmpLayerPdu = offset
            writeShort(0) // PDU length
            writeByte(VECTOR_DMP_SET_PROPERTY.toByte()) // Vector
            writeByte(0xA1.toByte()) // Address type & data type
            writeShort(0x0) // First property address
            writeShort(0x1) // Address increment
            writeShort(1 + colors.pixelCount * 3) // Property value count
            // Property values...
            writeByte(0x0) // START code
            for (color in colors) {
                writeByte(color.redB)
                writeByte(color.greenB)
                writeByte(color.blueB)
            }

            at(rootLayerPdu).writeShort(0x7000 or offset - rootLayerPdu)
            at(framingLayerPdu).writeShort(0x7000 or offset - framingLayerPdu)
            at(dmpLayerPdu).writeShort(0x7000 or offset - dmpLayerPdu)
        }.toBytes()

        udpSocket.sendUdp(address, sAcnPort, bytes)
    }

    fun readDataFrame(byteArray: ByteArray, pixels: Pixels) {
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
            readBytes(sourceNameBytes)
            readByte(/*199.toByte()*/) // Priority
            readShort(/*0x0*/) // Synchronization address
            readByte(/*sequenceNumber.toByte()*/) // Sequence number
            readByte(/*0x0*/) // Options
            readShort(/*0x1*/) // Universe

            // DMP Layer
//            val dmpLayerPdu = offset
            readShort(/*0*/) // PDU length
            readByte(/*VECTOR_DMP_SET_PROPERTY.toByte()*/) // Vector
            readByte(/*0xA1.toByte()*/) // Address type & data type
            readShort(/*0x0*/) // First property address
            readShort(/*0x1*/) // Address increment
            val pixelCount = readShort() / 3 - 1 - 3// (/*1 + colors.pixelCount * 3*/) // Property value count
            // Property values...
            readByte(/*0x0*/) // START code
            for (i in 0 until pixelCount) {
                pixels[i] = Color(
                    readByte(/*color.redB*/),
                    readByte(/*color.greenB*/),
                    readByte(/*color.blueB*/)
                )
            }

//            at(rootLayerPdu).writeShort(0x7000 or offset - rootLayerPdu)
//            at(framingLayerPdu).writeShort(0x7000 or offset - framingLayerPdu)
//            at(dmpLayerPdu).writeShort(0x7000 or offset - dmpLayerPdu)
        }
    }

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

        private const val VECTOR_ROOT_E131_DATA = 0x4
        private const val VECTOR_ROOT_E131_EXTENDED = 0x8

        private const val VECTOR_DMP_SET_PROPERTY = 0x2

        private const val VECTOR_E131_DATA_PACKET = 0x2

        private const val VECTOR_E131_EXTENDED_SYNCHRONIZATION = 0x1
        private const val VECTOR_E131_EXTENDED_DISCOVERY = 0x2
        private const val VECTOR_UNIVERSE_DISCOVERY_UNIVERSE_LIST = 0x1

//        E131_E131_UNIVERSE_DISCOVERY_INTERVAL = 10 seconds
//        E131_NETWORK_DATA_LOSS_TIMEOUT = 2.5 seconds
//        E131_DISCOVERY_UNIVERSE = 64214
//        ACN_SDT_MULTICAST_PORT = 5568

        private val logger = Logger<SacnLink>()
    }
}