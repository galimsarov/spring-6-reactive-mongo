package guru.springframework.spring6reactivemongo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Spring6ReactiveMongoApplication

fun main(args: Array<String>) {
    runApplication<Spring6ReactiveMongoApplication>(*args)
}
