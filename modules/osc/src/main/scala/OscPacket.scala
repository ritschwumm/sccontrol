package sccontrol.osc

enum OscPacket {
	case Message(message:OscMessage)
	case Bundle(bundle:OscBundle)
}
