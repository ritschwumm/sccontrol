package sccontrol.osc

import scutil.lang.*

trait OscHandler {
	// must not throw exceptions
	def handle(packet:Either[OscReceiveProblem,OscPacket]):Io[Unit]
}
