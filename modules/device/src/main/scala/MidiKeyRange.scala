package sccontrol.device

import sccontrol.midi.*

final case class MidiKeyRange(start:MidiKey, size:Int) {
	val end:MidiKey			= MidiKey(start.value+size-1)
	val all:Vector[MidiKey]	= MidiKey.vector(start, end)
}
