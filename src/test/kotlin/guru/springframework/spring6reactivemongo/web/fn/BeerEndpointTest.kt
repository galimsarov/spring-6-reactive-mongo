package guru.springframework.spring6reactivemongo.web.fn

import guru.springframework.spring6reactivemongo.model.BeerDTO
import guru.springframework.spring6reactivemongo.services.BeerServiceImplTest
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@AutoConfigureWebTestClient
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BeerEndpointTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun testPatchIdNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .patch()
            .uri(BeerRouterConfig.BEER_PATH_ID, 999)
            .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO::class.java)
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun testPatchIdFound() {
        val beerDTO: BeerDTO = getSavedTestBeer()

        webTestClient
            .mutateWith(mockOAuth2Login())
            .patch()
            .uri(BeerRouterConfig.BEER_PATH_ID, beerDTO.id)
            .body(Mono.just(beerDTO), BeerDTO::class.java)
            .exchange()
            .expectStatus().isNoContent()
    }

    @Test
    fun testDeleteNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .delete().uri(BeerRouterConfig.BEER_PATH_ID, 999).exchange().expectStatus().isNotFound()
    }

    @Test
    @Order(999)
    fun testDeleteBeer() {
        val beerDTO: BeerDTO = getSavedTestBeer()

        webTestClient
            .mutateWith(mockOAuth2Login())
            .delete().uri(BeerRouterConfig.BEER_PATH_ID, beerDTO.id).exchange().expectStatus().isNoContent()
    }

    @Test
    @Order(4)
    fun testUpdateBeerBadRequest() {
        val testBeer = getSavedTestBeer().apply { beerStyle = "" }

        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(BeerRouterConfig.BEER_PATH_ID, testBeer)
            .body(Mono.just(testBeer), BeerDTO::class.java)
            .exchange()
            .expectStatus().isBadRequest()
    }

    @Test
    fun testUpdateBeerNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(BeerRouterConfig.BEER_PATH_ID, 999)
            .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO::class.java)
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    @Order(3)
    fun testUpdateBeer() {
        val beerDTO = getSavedTestBeer()
        webTestClient
            .mutateWith(mockOAuth2Login())
            .put()
            .uri(BeerRouterConfig.BEER_PATH_ID, beerDTO.id)
            .body(Mono.just(beerDTO), BeerDTO::class.java)
            .exchange()
            .expectStatus().isNoContent()
    }

    @Test
    fun testCreateBeerBadData() {
        val testBeer = BeerServiceImplTest.getTestBeer().apply { beerName = "" }

        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(testBeer), BeerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isBadRequest()
    }

    @Test
    fun testCreateBeer() {
        val testDto = getSavedTestBeer()

        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(testDto), BeerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists("location")
    }

    @Test
    fun testGetByIdNotFound() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_PATH_ID, 999).exchange().expectStatus().isNotFound()
    }

    @Test
    @Order(2)
    fun testListBeersByStyle() {
        val beerStyle = "TEST"
        val testDTO = getSavedTestBeer().apply { this.beerStyle = beerStyle }

        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(testDTO), BeerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()

        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(
                UriComponentsBuilder.fromPath(BeerRouterConfig.BEER_PATH).queryParam("beerStyle", beerStyle).build()
                    .toUri()
            )
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals("Content-Type", "application/json")
            .expectBody().jsonPath("$.size()").value(equalTo(1))
    }

    @Test
    @Order(2)
    fun testListBeers() {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("Content-type", "application/json")
            .expectBody().jsonPath("$.size()").value(greaterThan(1))
    }

    @Test
    @Order(1)
    fun testGetById() {
        val (id) = getSavedTestBeer()

        webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_PATH_ID, id)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("Content-type", "application/json")
            .expectBody(BeerDTO::class.java)
    }


    fun getSavedTestBeer(): BeerDTO {
        webTestClient
            .mutateWith(mockOAuth2Login())
            .post().uri(BeerRouterConfig.BEER_PATH)
            .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()
            .returnResult(BeerDTO::class.java)

        return webTestClient
            .mutateWith(mockOAuth2Login())
            .get().uri(BeerRouterConfig.BEER_PATH).exchange()
            .returnResult(BeerDTO::class.java).responseBody.blockFirst() ?: BeerDTO()
    }
}