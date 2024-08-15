package sccontrol.osc

import java.net.*

import scutil.core.implicits.*
import scutil.lang.*
import scutil.concurrent.SimpleWorker

object OscServer {
	// TODO osc hardcoded
	val maxPacketSize	= 16384

	// TODO using throws
	def open(address:InetSocketAddress, handler:OscHandler):IoResource[Unit]	=
		for {
			socket	<-	IoResource.unsafe.releasable(
							if (address.getAddress.isMulticastAddress) {
								new MulticastSocket(address.getPort) doto { it =>
									it.joinGroup(
										new InetSocketAddress(address.getAddress, 0),
										null
									)

								}
							}
							else {
								new DatagramSocket(address)
							}
						)
			// TODO using throws SocketException
			_		<-	IoResource.delay { socket.setSoTimeout(100) }
			_		<-	receiveThread(handler, socket)
		}
		yield ()

	private def receiveThread(handler:OscHandler, socket:DatagramSocket):IoResource[Unit]	=
		for {
			buffer	<-	IoResource.delay { new Array[Byte](maxPacketSize) }
			packet	<-	IoResource.delay { new DatagramPacket(buffer, buffer.size) }
			_		<-	SimpleWorker.create(
							"osc receiver",
							Thread.NORM_PRIORITY,
							for {
								data	<-	receive(socket, packet, buffer)
								_		<-	data match {
												case Left(OscReceiveProblem.CaughtException(e:SocketTimeoutException))	=>
													// ignored
													Io.unit
												case x	=>
													handler.handle(x)
											}
							}
							yield true
						)
		}
		yield ()

	private def receive(socket:DatagramSocket, packet:DatagramPacket, buffer:Array[Byte]):Io[Either[OscReceiveProblem,OscPacket]]	=
		Io.delay {
			for {
				_			<- Catch.exception.in(socket.receive(packet))				.leftMap (OscReceiveProblem.CaughtException.apply)
				bytes		<- ByteString.sliceFromArray(buffer, 0, packet.getLength)	.toRight (OscReceiveProblem.ParseError("cannot make ByteString"))
				oscPacket	<- OscParser.parse(bytes)									.leftMap (OscReceiveProblem.ParseError.apply)
			}
			yield oscPacket
		}
}
