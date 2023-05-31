package guru.springframework.spring6reactivemongo.repositories

import guru.springframework.spring6reactivemongo.domain.Beer
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BeerRepository : ReactiveMongoRepository<Beer, String> {

    fun findFirstByBeerName(beerName: String): Mono<Beer>

    fun findByBeerStyle(beerStyle: String): Flux<Beer>
}