package sccontrol.osc

import scutil.core.implicits.*
import scutil.lang.*

import scparse.ng.*
import scparse.ng.binary.*

object OscParser {
	def parse(bytes:ByteString):Either[String,OscPacket]	=
		oscPacket.parseByteString(bytes).toEither.leftMap { (index, error) =>
			error.mkString(" / ") + " at " + index.toString
		}

	//------------------------------------------------------------------------------

	lazy val oscPacket:BinaryParser[OscPacket]	=
		named("osc packet") {
			oscPacketContent.phrase
		}

	lazy val oscPacketContent:BinaryParser[OscPacket]	=
		oscBundle.map(OscPacket.Bundle.apply) `orElse`
		oscMessage.map(OscPacket.Message.apply)

	lazy val oscBundle:BinaryParser[OscBundle]	=
		named("OscBundle") {
			for {
				_		<-	oscString.filter(_ == "#bundle").named("'#bundle'")
				time	<-	oscTimetag
				items	<-	bundlePacket.vector
			}
			yield OscBundle(time, items)
		}

	lazy val bundlePacket:BinaryParser[OscPacket]	=
		named("bundle packet") {
			for {
				size	<-	oscInt32
				output	<-	BinaryParser.takeByteString(size).nestByteString(oscPacket)
			}
			yield output
		}

	lazy val oscMessage:BinaryParser[OscMessage]	=
		named("OscMessage") {
			for {
				address		<-	oscAddressPattern
				typeTags	<-	oscTypeTags
				arguments	<-	Parser.traverseVector(typeTags)
			}
			yield OscMessage(address, arguments)
		}

	lazy val oscAddressPattern:BinaryParser[OscAddressPattern]	=
		named("OscAddressPattern") {
			oscString.filter(_.startsWith("/")).named("'/'").map(OscAddressPattern.apply)
		}

	lazy val oscTypeTags:BinaryParser[Vector[BinaryParser[OscArgument]]]	=
		named("type tag string") {
			oscString.filter(_.startsWith(",")).named("','").map { raw =>
				raw.substring(1).toVector.map {
					case 'i'	=> oscInt32		.map (OscArgument.Int32.apply)
					case 'h'	=> oscInt64		.map (OscArgument.Int64.apply)
					case 'f'	=> oscFloat32	.map (OscArgument.Float32.apply)
					case 'd'	=> oscFloat64	.map (OscArgument.Float64.apply)
					case 's'	=> oscString	.map (OscArgument.Str.apply)
					case 'S'	=> oscString	.map (OscArgument.Sym.apply)
					case 'b'	=> oscBlob		.map (OscArgument.Blob.apply)
					case 't'	=> oscTimetag	.map (OscArgument.Timetag.apply)
					case 'I'	=> Parser	.success (OscArgument.Bang)
					case 'T'	=> Parser	.success (OscArgument.True)
					case 'F'	=> Parser	.success (OscArgument.False)
					case 'N'	=> Parser	.success (OscArgument.Nil)
					case '['	=> Parser	.success (OscArgument.ArrayStart)
					case ']'	=> Parser	.success (OscArgument.ArrayEnd)
					case 'c'	=> oscInt32		.map (OscArgument.Char.apply)
					case 'r'	=> oscRgba		.map (OscArgument.RGBA.apply)
					case 'm'	=> oscMidi		.map (OscArgument.MIDI.apply)
					case x		=> Parser.failure.named("typeTag")
				}
			}
		}

	//------------------------------------------------------------------------------

	lazy val oscTimetag:BinaryParser[OscTimetag]	=
		oscInt64.map(OscTimetag.apply).named("OscTimetag")

	lazy val oscInt32:BinaryParser[Int]			= NumericParser.int32_be										.named("oscInt32")
	lazy val oscInt64:BinaryParser[Long]		= NumericParser.int64_be										.named("oscInt64")
	lazy val oscFloat32:BinaryParser[Float]		= NumericParser.int32_be.map(java.lang.Float.intBitsToFloat)	.named("oscFloat32")
	lazy val oscFloat64:BinaryParser[Double]	= NumericParser.int64_be.map(java.lang.Double.longBitsToDouble)	.named("oscFloat64")

	lazy val oscString:BinaryParser[String]	=
		named("String") {
			for {
				bytes	<-	BinaryParser.any.filter(_ != 0).vector.stringify
				_		<-	BinaryParser.any.filter(_ == 0)
				_		<-	BinaryParser.takeByteString(padding(bytes.size + 1, 4))
			}
			yield bytes.asUtf8String
		}

	lazy val oscBlob:BinaryParser[ByteString]	=
		named("Blob") {
			for {
				size	<-	oscInt32
				content	<-	BinaryParser.takeByteString(size)
				// TODO osc now about native (fast) skipping?
				_		<-	BinaryParser.takeByteString(padding(size, 4))
			}
			yield content
		}

	lazy val oscRgba:BinaryParser[OscRgba]	=
		BinaryParser.takeByteString(4).map { case ByteString(r,g,b,a) => OscRgba(r, g, b, a) } .named("OscRgba")

	lazy val oscMidi:BinaryParser[OscMidi]	=
		BinaryParser.takeByteString(4).map { case ByteString(port, status, data1, data2) => OscMidi(port, status, data1, data2) }.named("OscMidi")

	//------------------------------------------------------------------------------

	private def named[T](name:String)(base:BinaryParser[T]):BinaryParser[T]	= base.named(name)

	private def padding(size:Int, block:Int):Int	=
		(block-1) - (size + block-1) % block
}
