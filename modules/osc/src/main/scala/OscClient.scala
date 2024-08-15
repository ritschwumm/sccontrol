package sccontrol.osc

import java.net.*

import scutil.lang.*

object OscClient {
	// TODO using throws
	def open:IoResource[OscClient]	=
		IoResource.unsafe.releasable(new DatagramSocket()).map { socket =>
			(target:InetSocketAddress, oscPacket:OscPacket)	=> {
				val payload	= OscPrinter.oscPacket(oscPacket)
				Io.delay {
					val packet	= new DatagramPacket(payload.unsafeValue, payload.size, target)
					socket.send(packet)
				}
				.attempt
			}
		}
}

trait OscClient {
	/** must not be called after a call to #close */
	def send(target:InetSocketAddress, oscPacket:OscPacket):Io[Either[Exception,Unit]]
}
