package sccontrol.device.mpkMini

import scutil.lang.*

import sccontrol.midi.*

import sccontrol.device.mpkMini.protocol.*

object MpkMiniClient {
	val mock:MpkMiniClient	= _ => Io.pure(Right(()))

	val open:IoResource[Option[MpkMiniClient]]	=
		MidiClient.open(MpkMiniDevice.devicePredicate).map{ clientOpt =>
			clientOpt.map { client =>
				(output) => {
					client.send(Protocol.writeOutput(output), MidiTime.unsupported)
				}
			}
		}
}

trait MpkMiniClient {
	def send(output:Output):Io[Either[Exception,Unit]]
}
