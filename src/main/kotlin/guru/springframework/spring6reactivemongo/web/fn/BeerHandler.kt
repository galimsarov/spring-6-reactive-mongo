package guru.springframework.spring6reactivemongo.web.fn

import guru.springframework.spring6reactivemongo.model.BeerDTO
import guru.springframework.spring6reactivemongo.services.BeerService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class BeerHandler(private val beerService: BeerService) {
    fun listBeers(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(beerService.listBeers(), BeerDTO::class.java)
    }
}