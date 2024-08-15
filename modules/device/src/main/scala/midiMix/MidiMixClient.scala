package sccontrol.device.midiMix

//import scutil.core.implicits.*
import scutil.lang.*

import sccontrol.midi.*

import sccontrol.device.midiMix.protocol.*

object MidiMixClient {
	val mock:MidiMixClient	= _ => Io.pure(Right(()))

	val open:IoResource[Option[MidiMixClient]]	=
		MidiClient.open(MidiMixDevice.devicePredicate).map{ clientOpt =>
			clientOpt.map { client =>
				(output) => {
					client.send(Protocol.writeOutput(output), MidiTime.unsupported)
				}
			}
		}
}

trait MidiMixClient {
	def send(output:Output):Io[Either[Exception,Unit]]
}
