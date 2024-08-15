package sccontrol.osc

import scutil.time.*

object OscTimetag {
	/** sentinel value meaning "do this immediately" */
	val immediately = OscTimetag(1)

	/** time of the beginning of NTP era 0 (1900-01-01T00:00:00.000Z) in milliseconds since the unix epoch (1970-01-01T00:00:00.000Z) */
	private val startOfEra0 = -2208988800000L

	/** time of the beginning of NTP era 1 (2036-02-07T06:28:16.xxxT in milliseconds since the unix epoch (1970-01-01T00:00:00.000Z) */
	private val startOfEra1	= 2085978496000L

	def fromMilliInstant(it:MilliInstant):OscTimetag	=
		fromEpochMillis(it.millis)

	// TODO osc simplify:
	//  we are essentially just doing ((time - startOfEra1) << 32) / 1000
	// in signed math - the real problem is dealing with overflows
	def fromEpochMillis(it:Long):OscTimetag =
		OscTimetag(
			if (it < startOfEra1)	div1000(it - startOfEra0) | (1L << 63)
			else					div1000(it - startOfEra1)
		)

	private def div1000(it:Long):Long	= {
		val int		= it					/ 1000
		val fract	= ((it % 1000) << 32)	/ 1000
		int << 32 | fract
	}
}

/**
 * the value is the number of seconds since 1900-01-01T00:00.000Z encoded as an unsigned
 * fixpoint number with 32 bits before and 32 bits after the decimal point.
 *
 * per spec this is an NTP timestamp, but nothing is said about rollover when
 * the 32 bits for the integral seconds will run out.
 * therefore here we assume that NTP eras have to be supported.
 *
 * note that rollover in NTP timestamps is actually quite clever:
 * if treating the value as a simple, signed long, then all times in era 0
 * are negative and all times in era 1 are positive (including 0)
 */
final case class OscTimetag(value:Long) extends Ordered[OscTimetag] {
	def toMilliInstant:MilliInstant = MilliInstant(toEpochMillis)

	// TODO osc simplify:
	// we are essentially just doing ((time * 1000 ) >> 32) + startOfEra1
	// in signed math - the real problem is dealing with overflows
	def toEpochMillis:Long	= {
		val seconds		= (value >>> 32) & 0xffffffffL
		val fraction1	= (value >>>  0) & 0xffffffffL

		val fraction	= math.round(1000D * fraction1 / (1L << 32))
		val offset		= (seconds * 1000) + fraction

		val msb = (value & (1L << 63)) != 0
		val startOfEra	=
			if (msb)	OscTimetag.startOfEra0
			else		OscTimetag.startOfEra1

		startOfEra + offset
	}

	def compare(that:OscTimetag):Int	=
		this.value.compareTo(that.value)

	/**
	* returns Left(false) for "negative infinity", Left(true) for "positive infinity" and Right(x) for the difference in ticks
	* this is necessary because we are dealing with an unsigned Long value, and Long is not large enough to represent a signed
	* difference between two of those.
	*/
	def ticksUntil(that:OscTimetag):Either[Boolean,Long]	= {
		val raw	= that.value - this.value
		if		(that.value > this.value && raw < 0)	Left(true)
		else if	(that.value < this.value && raw > 0)	Left(false)
		else											Right(raw)
	}
}
