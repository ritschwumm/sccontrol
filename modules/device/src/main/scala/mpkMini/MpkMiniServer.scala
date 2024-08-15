package sccontrol.device.mpkMini

import scutil.core.implicits.*
import scutil.lang.*

import sccontrol.midi.*

import sccontrol.device.mpkMini.protocol.*

object MpkMiniServer {
	def open(onInput:Input=>Io[Unit]):IoResource[Boolean]	=
		MidiServer.open(
			MpkMiniDevice.devicePredicate,
			(event:MidiEvent, time:MidiTime)	=> {
				Protocol.readInput(event).traverseVoid(onInput)
			}
		)
}
