package guru.springframework.spring6reactivemongo.web.fn

import guru.springframework.spring6reactivemongo.model.CustomerDTO
import guru.springframework.spring6reactivemongo.services.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CustomerHandler(private val customerService: CustomerService, private val validator: Validator) {
    private fun validate(customerDTO: CustomerDTO) {
        val errors: Errors = BeanPropertyBindingResult(customerDTO, "customerDto")
        validator.validate(customerDTO, errors)
        if (errors.hasErrors()) {
            throw ServerWebInputException(errors.toString())
        }
    }

    fun listCustomers(request: ServerRequest): Mono<ServerResponse> {
        val flux: Flux<CustomerDTO> =
            if (request.queryParam("customerName").isPresent) {
                customerService.findByCustomerName(request.queryParam("customerName").get())
            } else {
                customerService.listCustomers()
            }
        return ServerResponse.ok().body(flux, CustomerDTO::class.java)
    }

    fun getCustomerById(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(
            customerService.getById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))), CustomerDTO::class.java
        )
    }

    fun createNewCustomer(request: ServerRequest): Mono<ServerResponse> {
        return customerService.saveCustomer(request.bodyToMono(CustomerDTO::class.java).doOnNext { validate(it) })
            .flatMap { customerDto ->
                ServerResponse.created(
                    UriComponentsBuilder.fromPath(CustomerRouterConfig.CUSTOMER_PATH_ID).build(customerDto.id)
                ).build()
            }
    }

    fun updateCustomerById(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(CustomerDTO::class.java)
            .doOnNext { validate(it) }
            .flatMap { customerDto -> customerService.updateCustomer(request.pathVariable("customerId"), customerDto) }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
            .flatMap { ServerResponse.noContent().build() }
    }

    fun patchCustomerById(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(CustomerDTO::class.java)
            .doOnNext { validate(it) }
            .flatMap { customerDto -> customerService.patchCustomer(request.pathVariable("customerId"), customerDto) }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
            .flatMap { ServerResponse.noContent().build() }
    }

    fun deleteCustomerById(request: ServerRequest): Mono<ServerResponse> {
        return customerService
            .getById(request.pathVariable("customerId"))
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
            .flatMap { customerService.deleteCustomerById(it.id) }
            .then(ServerResponse.noContent().build())
    }
}