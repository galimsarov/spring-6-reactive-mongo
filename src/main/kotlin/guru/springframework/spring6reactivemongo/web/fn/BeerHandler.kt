package guru.springframework.spring6reactivemongo.web.fn

import guru.springframework.spring6reactivemongo.model.BeerDTO
import guru.springframework.spring6reactivemongo.services.BeerService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class BeerHandler(private val beerService: BeerService) {
    fun listBeers(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(beerService.listBeers(), BeerDTO::class.java)
    }

    fun getBeerById(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(beerService.getById(request.pathVariable("beerId")), BeerDTO::class.java)
    }

    fun createNewBeer(request: ServerRequest): Mono<ServerResponse> {
        return beerService.saveBeer(request.bodyToMono(BeerDTO::class.java))
            .flatMap { beerDto ->
                ServerResponse.created(UriComponentsBuilder.fromPath(BeerRouterConfig.BEER_PATH_ID).build(beerDto.id))
                    .build()
            }
    }

    fun updateBeerById(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(BeerDTO::class.java)
            .flatMap { beerDto -> beerService.updateBeer(request.pathVariable("beerId"), beerDto) }
            .flatMap { ServerResponse.noContent().build() }
    }
}