package sccontrol.osc

import scutil.core.implicits.*

/*
TODO osc how about a vector of parts instead?

TODO osc restrict permitted characters.

an OscMethod always starts with a slash and is followed by slash-separated container names
a container name consists of any number of printable ASCII characters except ' '	#	*	,	/	?	[]	{}
a container with an empty name might mean path traversal (like in xquery) in an OscAddressPattern

an OscAddressPattern looks like an OscMethod but has additional metacharacters:
? matches a single character
* matches zero or more characters
[string] is a character class, inside a-b is a range, a - at the end is a literal -, a ! at the begin negates the class
{foo,bar} matches either foo or bar
*/
final case class OscAddressPattern(path:String) {
	require(path.startsWith("/"), so"expected a '/' at the start, found $path")
}
