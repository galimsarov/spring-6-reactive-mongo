package guru.springframework.spring6reactivemongo.repositories

import guru.springframework.spring6reactivemongo.domain.Customer
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface CustomerRepository : ReactiveMongoRepository<Customer, String>