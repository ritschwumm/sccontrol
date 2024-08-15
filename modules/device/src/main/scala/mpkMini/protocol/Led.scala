package sccontrol.device.mpkMini.protocol

object Led {
	val midiChannel	= Pad.midiChannel

	val all	= Pad.all.map(Led.apply)
}

// TODO device this is not a good solution yet
final case class Led(pad:Pad) {
	def midiKey	= pad.midiKey
}
