package sccontrol.device.midiMix.protocol

import sccontrol.midi.*

enum Input {
	case ButtonChange(button:Button, on:Boolean)
	case ControlChange(control:Control, value:MidiValue)
}
