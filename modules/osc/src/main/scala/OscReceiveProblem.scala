package sccontrol.osc

enum OscReceiveProblem {
	case CaughtException(exception:Exception)
	case ParseError(messages:String)
}
