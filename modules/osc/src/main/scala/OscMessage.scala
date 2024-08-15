package sccontrol.osc

final case class OscMessage(
	addressPattern:OscAddressPattern,
	arguments:Vector[OscArgument]
)
