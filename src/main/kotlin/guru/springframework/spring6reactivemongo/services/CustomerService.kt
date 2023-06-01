package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.model.CustomerDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomerService {
    fun listCustomers(): Flux<CustomerDTO>
    fun getById(customerId: String): Mono<CustomerDTO>
    fun saveCustomer(customerDTO: Mono<CustomerDTO>): Mono<CustomerDTO>
    fun saveCustomer(customerDTO: CustomerDTO): Mono<CustomerDTO>
    fun updateCustomer(customerId: String, customerDTO: CustomerDTO): Mono<CustomerDTO>
    fun patchCustomer(customerId: String, customerDTO: CustomerDTO): Mono<CustomerDTO>
    fun deleteCustomerById(customerId: String): Mono<Void>
    fun findByCustomerName(customerName: String): Flux<CustomerDTO>
}