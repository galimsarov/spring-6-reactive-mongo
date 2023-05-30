package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.model.BeerDTO
import reactor.core.publisher.Mono

interface BeerService {
    fun saveBeer(beerDTO: BeerDTO): Mono<BeerDTO>

    fun getById(beerId: String): Mono<BeerDTO>
}