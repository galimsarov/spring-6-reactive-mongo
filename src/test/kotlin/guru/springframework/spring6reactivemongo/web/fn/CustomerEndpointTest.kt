package guru.springframework.spring6reactivemongo.web.fn

import guru.springframework.spring6reactivemongo.model.CustomerDTO
import guru.springframework.spring6reactivemongo.services.CustomerServiceImplTest
import org.hamcrest.Matchers
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@AutoConfigureWebTestClient
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CustomerEndpointTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun testPatchIdNotFound() {
        webTestClient.patch()
            .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
            .body(Mono.just(CustomerServiceImplTest.getTestCustomer()), CustomerDTO::class.java)
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun testPatchIdFound() {
        val customerDTO: CustomerDTO = getSavedTestCustomer()

        webTestClient.patch()
            .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.id)
            .body(Mono.just(customerDTO), CustomerDTO::class.java)
            .exchange()
            .expectStatus().isNoContent()
    }

    @Test
    fun testDeleteNotFound() {
        webTestClient.delete().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999).exchange().expectStatus().isNotFound()
    }

    @Test
    @Order(999)
    fun testDeleteCustomer() {
        val customerDTO: CustomerDTO = getSavedTestCustomer()

        webTestClient.delete().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.id).exchange().expectStatus()
            .isNoContent()
    }

    @Test
    @Order(4)
    fun testUpdateCustomerBadRequest() {
        val testCustomer = getSavedTestCustomer().apply { customerName = "" }

        webTestClient.put()
            .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testCustomer)
            .body(Mono.just(testCustomer), CustomerDTO::class.java)
            .exchange()
            .expectStatus().isBadRequest()
    }

    @Test
    fun testUpdateCustomerNotFound() {
        webTestClient.put()
            .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
            .body(Mono.just(CustomerServiceImplTest.getTestCustomer()), CustomerDTO::class.java)
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    @Order(3)
    fun testUpdateCustomer() {
        val customerDTO = getSavedTestCustomer()
        webTestClient.put()
            .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.id)
            .body(Mono.just(customerDTO), CustomerDTO::class.java)
            .exchange()
            .expectStatus().isNoContent()
    }

    @Test
    fun testCreateCustomerBadData() {
        val testCustomer = CustomerServiceImplTest.getTestCustomer().apply { customerName = "" }

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(testCustomer), CustomerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isBadRequest()
    }

    @Test
    fun testCreateCustomer() {
        val testDto = getSavedTestCustomer()

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(testDto), CustomerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists("location")
    }

    @Test
    fun testGetByIdNotFound() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999).exchange().expectStatus().isNotFound()
    }

    @Test
    @Order(2)
    fun testListCustomersByName() {
        val customerName = "TEST"
        val testDTO = getSavedTestCustomer().apply { this.customerName = customerName }

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(testDTO), CustomerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()

        webTestClient.get().uri(
            UriComponentsBuilder.fromPath(CustomerRouterConfig.CUSTOMER_PATH).queryParam("customerName", customerName)
                .build().toUri()
        )
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals("Content-Type", "application/json")
            .expectBody().jsonPath("$.size()").value(Matchers.equalTo(1))
    }

    @Test
    @Order(2)
    fun testListCustomers() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("Content-type", "application/json")
            .expectBody().jsonPath("$.size()").value(Matchers.greaterThan(1))
    }

    @Test
    @Order(1)
    fun testGetById() {
        val (id) = getSavedTestCustomer()

        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, id)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("Content-type", "application/json")
            .expectBody(CustomerDTO::class.java)
    }

    fun getSavedTestCustomer(): CustomerDTO {
        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
            .body(Mono.just(CustomerServiceImplTest.getTestCustomer()), CustomerDTO::class.java)
            .header("Content-Type", "application/json")
            .exchange()
            .returnResult(CustomerDTO::class.java)

        return webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH).exchange()
            .returnResult(CustomerDTO::class.java).responseBody.blockFirst() ?: CustomerDTO()
    }

}