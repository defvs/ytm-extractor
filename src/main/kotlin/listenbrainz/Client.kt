package listenbrainz

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.lens.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonNamingStrategy
import org.http4k.client.Java8HttpClient

class Client(private val token: String, private val chunkSize: Int = 200) {
    @OptIn(ExperimentalSerializationApi::class) val json = Json {
        namingStrategy = JsonNamingStrategy.SnakeCase
        encodeDefaults = true
    }

    private val baseURL = "https://api.listenbrainz.org"

    val httpClient = Java8HttpClient()

    fun sendListens(listens: List<ListenBrainzPayload>) {
        listens.chunked(chunkSize).forEachIndexed { i, chunk ->
            val request = Request(Method.POST, "$baseURL/1/submit-listens")
                .contentType(ContentType.APPLICATION_JSON)
                .header("Authorization", "Token $token")
                .body(json.encodeToString(ListenBrainzListens(payload = chunk)))

            println("Chunk $i: response=${httpClient(request).bodyString()}")
            println("from ${chunk.minOf { it.listenedAt.epochSecond }} to ${chunk.maxOf { it.listenedAt.epochSecond }}")

            Thread.sleep(5000)
        }
    }
}