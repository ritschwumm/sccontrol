package sccontrol.osc

import scutil.lang.*

enum OscArgument {
	// required since OSC 1.0
	case Int32(value:Int)
	// required since OSC 1.0
	case Float32(value:Float)
	// basic type in OSC 1.0
	case Str(value:String)
	// basic type in OSC 1.0
	case Blob(value:ByteString)

	// required since OSC 1.1
	case True
	// required since OSC 1.1
	case False
	// required since OSC 1.1
	case Nil
	// required since OSC 1.1, was named Infinitum in OSC 1.0
	case Bang
	// required in OSC 1.1
	case Timetag(value:OscTimetag)

	// non-standard
	case Int64(value:Long)
	// non-standard
	case Float64(value:Double)

	// non-standard
	case ArrayStart
	// non-standard
	case ArrayEnd

	// non-standard
	case Sym(value:String)
	// non-standard
	case Char(value:Int)
	// non-standard
	case RGBA(value:OscRgba)
	// non-standard
	case MIDI(value:OscMidi)
}
