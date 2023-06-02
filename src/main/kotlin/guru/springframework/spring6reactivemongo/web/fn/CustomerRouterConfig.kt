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
class CustomerRouterConfig(private val handler: CustomerHandler) {
    @Bean
    fun customerRoutes(): RouterFunction<ServerResponse> {
        return RouterFunctions.route()
            .GET(CUSTOMER_PATH, RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::listCustomers)
            .GET(CUSTOMER_PATH_ID, RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::getCustomerById)
            .POST(CUSTOMER_PATH, RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::createNewCustomer)
            .PUT(CUSTOMER_PATH_ID, RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::updateCustomerById)
            .PATCH(CUSTOMER_PATH_ID, RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::patchCustomerById)
            .DELETE(CUSTOMER_PATH_ID, RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::deleteCustomerById)
            .build()
    }

    companion object {
        const val CUSTOMER_PATH = "/api/v3/customer"
        const val CUSTOMER_PATH_ID = "$CUSTOMER_PATH/{customerId}"
    }
}