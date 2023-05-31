package guru.springframework.spring6reactivemongo.web.fn

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
@Suppress("unused")
class BeerRouterConfig(private val handler: BeerHandler) {
    @Bean
    fun beerRoutes(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .GET(BEER_PATH, accept(APPLICATION_JSON), handler::listBeers)
            .GET(BEER_PATH_ID, accept(APPLICATION_JSON), handler::getBeerById)
            .POST(BEER_PATH, accept(APPLICATION_JSON), handler::createNewBeer)
            .build()
    }

    companion object {
        const val BEER_PATH = "/api/v3/beer"
        const val BEER_PATH_ID = "$BEER_PATH/{beerId}"
    }
}