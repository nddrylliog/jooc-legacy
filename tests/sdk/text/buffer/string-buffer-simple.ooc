import text/StringBuffer

main: func {

	sb := Buffer new()
	for(i in 0..10) {
		sb append("yay =D ")
	}
	sb toString() println()

}
