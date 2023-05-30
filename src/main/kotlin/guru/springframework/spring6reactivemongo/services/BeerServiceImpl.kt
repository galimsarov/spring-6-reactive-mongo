package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.model.BeerDTO
import reactor.core.publisher.Mono

class BeerServiceImpl : BeerService {
    override fun saveBeer(beerDTO: BeerDTO): Mono<BeerDTO> {
        TODO("Not yet implemented")
    }

    override fun getById(beerId: String): Mono<BeerDTO> {
        TODO("Not yet implemented")
    }
}