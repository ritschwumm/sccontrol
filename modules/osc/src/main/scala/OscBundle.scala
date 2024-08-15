package sccontrol.osc

final case class OscBundle(
	timetag:OscTimetag,
	elements:Vector[OscPacket]
)
