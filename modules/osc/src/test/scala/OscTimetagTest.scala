package sccontrol.osc

import minitest.*

import scutil.time.*

object OscTimetagTest extends SimpleTestSuite {
	// 2020-03-26
	val timeInEra0		= MilliInstant(1585215530443L)

	// 68 years after 1970
	val timeInEra1		= MilliInstant.zero +! (MilliDuration.day *! (365 * 68))

	val timeBeforeUnix	= MilliInstant(-1000)

	val startOfEra1		= 2085978496000L

	test("OscTimetag should") {
		val b = OscTimetag.fromEpochMillis(startOfEra1)
		assertEquals(b.value, 0L)
	}

	test("OscTimetag should") {
		val b	= OscTimetag.fromMilliInstant(timeInEra0)
		assert(b.value < 0L)
	}

	test("OscTimetag should") {
		val b	= OscTimetag.fromMilliInstant(timeInEra1)
		assert(b.value >= 0L)
	}

	test("OscTimetag should") {
		assertEquals(
			OscTimetag(-10).ticksUntil(OscTimetag(+15)),
			Right(25L)
		)
	}

	test("OscTimetag should") {
		val large	= (1L << 62) + 1
		assertEquals(
			OscTimetag(-large).ticksUntil(OscTimetag(+large)),
			Left(true)
		)
	}

	test("OscTimetag should") {
		val large	= (1L << 62) + 1
		assertEquals(
			OscTimetag(+large).ticksUntil(OscTimetag(-large)),
			Left(false)
		)
	}

	test("OscTimetag should") {
		val a	= MilliInstant.now()
		val b	= OscTimetag.fromMilliInstant(a)
		val c	= b.toMilliInstant
		assertEquals(c, a)
	}

	test("OscTimetag should") {
		val a	= MilliInstant.zero
		val b	= OscTimetag.fromMilliInstant(a)
		val c	= b.toMilliInstant
		assertEquals(c, a)
	}

	test("OscTimetag should") {
		val a	= timeBeforeUnix
		val b	= OscTimetag.fromMilliInstant(a)
		val c	= b.toMilliInstant
		assertEquals(c, a)
	}

	test("OscTimetag should") {
		val a	= timeInEra1
		val b	= OscTimetag.fromMilliInstant(a)
		val c	= b.toMilliInstant
		assertEquals(c, a)
	}
}
