package sccontrol.osc

import scutil.core.implicits.*
import scutil.lang.*

object OscPrinter {
	def oscPacket(packet:OscPacket):ByteString	=
		packet match {
			case OscPacket.Message(message)	=> oscMessage(message)
			case OscPacket.Bundle(bundle)	=> oscBundle(bundle)
		}

	def oscBundle(bundle:OscBundle):ByteString	=
		oscString("#bundle")		++
		oscTimetag(bundle.timetag)	++
		(
			bundle.elements
			.map { it =>
				val content	= oscPacket(it)
				oscInt32(content.size) ++ content
			}
			.combineAll
		)

	def oscMessage(message:OscMessage):ByteString	=
		oscString(message.addressPattern.path)										++
		oscString(message.arguments.map(oscArgumentTypeTag).mkString(",", "", ""))	++
		message.arguments.map(oscArgumentContent).combineAll

	def oscArgumentTypeTag(argument:OscArgument):Char	=
		argument match {
			case OscArgument.Int32(_)	=> 'i'
			case OscArgument.Int64(_)	=> 'h'
			case OscArgument.Float32(_)	=> 'f'
			case OscArgument.Float64(_)	=> 'd'
			case OscArgument.Str(_)		=> 's'
			case OscArgument.Sym(_)		=> 'S'
			case OscArgument.Blob(_)	=> 'b'
			case OscArgument.Timetag(_)	=> 't'
			case OscArgument.Bang		=> 'I'
			case OscArgument.True		=> 'T'
			case OscArgument.False		=> 'F'
			case OscArgument.Nil		=> 'N'
			case OscArgument.ArrayStart	=> '['
			case OscArgument.ArrayEnd	=> ']'
			case OscArgument.Char(_)	=> 'c'
			case OscArgument.RGBA(_)	=> 'r'
			case OscArgument.MIDI(_)	=> 'm'
		}

	def oscArgumentContent(argument:OscArgument):ByteString	=
		argument match {
			case OscArgument.Int32(value)	=> oscInt32(value)
			case OscArgument.Int64(value)	=> oscInt64(value)
			case OscArgument.Float32(value)	=> oscFloat32(value)
			case OscArgument.Float64(value)	=> oscFloat64(value)
			case OscArgument.Str(value)		=> oscString(value)
			case OscArgument.Sym(value)		=> oscSymbol(value)
			case OscArgument.Blob(value)	=> oscBlob(value)
			case OscArgument.Timetag(value)	=> oscTimetag(value)
			case OscArgument.Bang			=> ByteString.empty
			case OscArgument.True			=> ByteString.empty
			case OscArgument.False			=> ByteString.empty
			case OscArgument.Nil			=> ByteString.empty
			case OscArgument.ArrayStart		=> ByteString.empty
			case OscArgument.ArrayEnd		=> ByteString.empty
			case OscArgument.Char(value)	=> oscInt32(value)
			case OscArgument.RGBA(value)	=> oscRgba(value)
			case OscArgument.MIDI(value)	=> oscMidi(value)
		}

	//------------------------------------------------------------------------------

	def oscTimetag(it:OscTimetag):ByteString	=
		oscInt64(it.value)

	def oscInt32(it:Int):ByteString	=
		ByteString.fromBigEndianInt(it)

	def oscInt64(it:Long):ByteString	=
		ByteString.fromBigEndianLong(it)

	def oscFloat32(it:Float):ByteString	=
		ByteString.fromBigEndianInt(java.lang.Float.floatToRawIntBits(it))

	def oscFloat64(it:Double):ByteString	=
		ByteString.fromBigEndianLong(java.lang.Double.doubleToRawLongBits(it))

	def oscString(s:String):ByteString	=
		zeroPad((ByteString.fromUtf8String(s)) ++ zero, 4)

	def oscSymbol(s:String):ByteString	=
		zeroPad((ByteString.fromUtf8String(s)) ++ zero, 4)

	def oscBlob(it:ByteString):ByteString	=
		oscInt32(it.size) ++ zeroPad(it, 4)

	def oscRgba(it:OscRgba):ByteString	=
		ByteString.of(it.r, it.g, it.b, it.a)

	def oscMidi(it:OscMidi):ByteString	=
		ByteString.of(it.port, it.status, it.data1, it.data2)

	//------------------------------------------------------------------------------

	private val zero	= ByteString.of(0)

	private def zeroPad(s:ByteString, block:Int):ByteString	=
		s ++ times(zero, padding(s.size, block))

	private def padding(size:Int, block:Int):Int	=
		(block-1) - (size + block-1) % block

	private def times(s:ByteString, count:Int):ByteString	=
		s.times(count)
}
