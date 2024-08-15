package sccontrol.osc

import java.net.*
import minitest.*

import scutil.core.implicits.*
import scutil.jdk.implicits.*
import scutil.lang.*

object OscServerTest extends SimpleTestSuite {
	test("OscServer should accept messages") {
		var received:Either[OscReceiveProblem,OscPacket]	= null

		val address		= InetAddress.getLoopbackAddress().socketAddress(4711)
		OscServer.open(address, packet => Io.delay { received = packet })
		.use { _ =>
			Io.sleep(500.millis)
		}
		.unsafeRun()

		assert(received eq null)
	}
}
