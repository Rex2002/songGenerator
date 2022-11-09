# songGenerator
SongGenerator source code for SoftwareEngineering I


## Interface Specifications
This lists the public specifications of component interfaces.

### Music components

Structure:

	class Structure

		List(Part) parts
  
		Enum(Key) key
  
		Enum(Genre) genre

Part:

	class Part

		int length            //in 4/4-Bars, usually multiple of 4

		List(Chord) chords

		List(Enum(Instrument)) instruments
