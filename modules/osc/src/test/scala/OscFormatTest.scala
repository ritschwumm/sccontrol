package sccontrol.osc

import minitest.*

import scutil.core.implicits.*
import scutil.codec.*

object OscFormatTest extends SimpleTestSuite {
	test("OSC should roundtrip format and parse") {
		val packet	=
			OscPacket.Message(
				OscMessage(
					OscAddressPattern("/test"),
					Vector[OscArgument](
						OscArgument.Int32(4711),
						OscArgument.Float64(1.0)
					)
				)
			)
		val bytes	= OscPrinter.oscPacket(packet)
		val parsed	= OscParser.parse(bytes)

		assertEquals(parsed, Right(packet))
	}

	/*
	# oscAddressPattern
	2f 64 69 65	/die
	00			<end-string>
	00 00 00	<padding>
	# oscTypeTags
	2c			,
	00			<end-string>
	00 00		<padding>
	*/
	test("OSC should parse a message") {
		val bytes	= Hex.decodeByteString("2f646965000000002c000000").getOrError("oops")
		val parsed	= OscParser.parse(bytes)
		assertEquals(
			parsed,
			Right(OscPacket.Message(OscMessage(OscAddressPattern("/die"), Vector())))
		)
	}

	test("OSC should parse a string") {
		import scparse.ng.binary.*

		val parser	=
			for {
				path	<-	OscParser.oscString
				types	<-	OscParser.oscString
			}
			yield (path, types)

		val bytes	= Hex.decodeByteString("2f 64 69 65 00 00 00 00 2c 00 00 00".replace(" ", "")).getOrError("oops")

		val parsed = parser.parseByteString(bytes).toEither
		assertEquals(
			parsed,
			Right(("/die", ","))
		)
	}
}
