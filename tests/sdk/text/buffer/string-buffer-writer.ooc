import io/Writer
import text/StringBuffer

write: func (writer: Writer) {
    for(i in 0..10) {
        writer write("yay =D ")
    }
}

main: func {
    sb := BufferWriter new()
    write(sb)
    sb buffer() toString() println()
}
