package sccontrol.device.mpkMini

import scutil.core.implicits.*
import scutil.lang.*

import sccontrol.midi.*

object MpkMiniDevice {
		// TODO device hardcoded
	val devicePredicate:Predicate[MidiDeviceInfo]	= {
		val linuxPredicate:Predicate[MidiDeviceInfo]	= _.getName.startsWith("mini ")
		val osxPredicate:Predicate[MidiDeviceInfo]		= _.getName == "MPK mini"
		linuxPredicate || osxPredicate
	}
}
