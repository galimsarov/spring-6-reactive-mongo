package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.model.BeerDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BeerService {
    fun listBeers(): Flux<BeerDTO>
    fun saveBeer(beerDTO: Mono<BeerDTO>): Mono<BeerDTO>
    fun saveBeer(beerDTO: BeerDTO): Mono<BeerDTO>

    fun getById(beerId: String): Mono<BeerDTO>
    fun updateBeer(beerId: String, beerDTO: BeerDTO): Mono<BeerDTO>
    fun patchBeer(beerId: String, beerDTO: BeerDTO): Mono<BeerDTO>
    fun deleteBeerById(beerId: String): Mono<Void>
}