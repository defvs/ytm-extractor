import java.util.function.Function
import java.util.stream.Stream

fun String.sanitize(): String = this
    .replace('\u00A0', ' ') // Replace non-breaking spaces with regular spaces
    .replace('\u202F', ' ') // Replace non-breaking spaces with regular spaces
    .replace('‬', ' ')
    .trim()

fun <T, R, R2: R> Stream<T>.mapNotNull(mapper: Function<in T, R2?>): Stream<R> {
    return this.flatMap { value ->
        mapper.apply(value)?.let { Stream.of(it) } ?: Stream.empty()
    }
}