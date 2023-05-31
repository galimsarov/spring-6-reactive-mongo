package guru.springframework.spring6reactivemongo.web.fn

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
@Suppress("unused")
class BeerRouterConfig(private val handler: BeerHandler) {
    @Bean
    fun beerRoutes(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .GET(BEER_PATH, RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::listBeers).build()
    }

    companion object {
        const val BEER_PATH = "/api/v3/beer"
        const val BEER_PATH_ID = "$BEER_PATH/{beerId}"
    }
}