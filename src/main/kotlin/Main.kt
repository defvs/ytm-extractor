import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import listenbrainz.Client
import youtube.YTMHTMLParser
import java.io.File
import java.time.Instant

class MyArgs(parser: ArgParser) {
    val inputFile by parser.storing(
        "-i", "--input",
        help = "Path to the input HTML file"
    ) { File(this) }

    val userToken by parser.storing(
        "-t", "--token",
        help = "User Token for ListenBrainz API"
    )

    val chunkSize by parser.storing(
        "--chunk-size",
        help = "ListenBrainz API chunk size"
    ) { this.toInt() }.default(200)

    val filterBefore: Instant by parser.storing(
        "--before",
        help = "Filter listens before this date (unix seconds)"
    ) { Instant.ofEpochSecond(this.toLong()) }.default(Instant.MAX)

    val filterAfter: Instant by parser.storing(
        "--after",
        help = "Filter listens after this date (unix seconds)"
    ) { Instant.ofEpochSecond(this.toLong()) }.default(Instant.MIN)
}

fun main(args: Array<String>) {
    ArgParser(args).parseInto(::MyArgs).run {
        if (!inputFile.exists()) {
            println("The file at path '${inputFile.path}' does not exist.")
            return
        }

        val data = YTMHTMLParser.parse(inputFile.readText())
            .filter {
            it.dateTime.isAfter(filterAfter) && it.dateTime.isBefore(filterBefore)
        }

        with(Client(userToken, chunkSize)) {
            sendListens(data.map { it.toListenBrainz() })
        }
    }
}
