package sccontrol.device.midiMix

import scutil.core.implicits.*
import scutil.lang.*

import sccontrol.midi.*

import sccontrol.device.midiMix.protocol.*

object MidiMixServer {
	def open(onInput:Input=>Io[Unit]):IoResource[Boolean]	=
		MidiServer.open(
			MidiMixDevice.devicePredicate,
			(event:MidiEvent, time:MidiTime)	=> {
				Protocol.readInput(event).traverseVoid(onInput)
			}
		)
}
