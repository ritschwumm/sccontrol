package sccontrol.device.mpkMini.protocol

import sccontrol.midi.*

import sccontrol.device.MidiKeyRange

object Keyboard {
	val midiChannel:MidiChannel	= MidiChannel(0)

	// unless shifted!
	val midiKeys:MidiKeyRange	= MidiKeyRange(MidiKey(48), 25)
}
