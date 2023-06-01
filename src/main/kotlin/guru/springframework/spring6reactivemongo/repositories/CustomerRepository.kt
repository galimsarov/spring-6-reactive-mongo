package guru.springframework.spring6reactivemongo.repositories

import guru.springframework.spring6reactivemongo.domain.Customer
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface CustomerRepository : ReactiveMongoRepository<Customer, String> {
    fun findByCustomerName(customerName: String): Flux<Customer>
}