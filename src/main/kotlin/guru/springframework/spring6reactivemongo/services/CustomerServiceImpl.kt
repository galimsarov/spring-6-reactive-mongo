package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.mappers.toCustomer
import guru.springframework.spring6reactivemongo.mappers.toCustomerDto
import guru.springframework.spring6reactivemongo.model.CustomerDTO
import guru.springframework.spring6reactivemongo.repositories.CustomerRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
@Suppress("unused")
class CustomerServiceImpl(private val customerRepository: CustomerRepository) : CustomerService {
    override fun listCustomers(): Flux<CustomerDTO> {
        return customerRepository.findAll().map { it.toCustomerDto() }
    }

    override fun getById(customerId: String): Mono<CustomerDTO> {
        return customerRepository.findById(customerId).map { it.toCustomerDto() }
    }

    override fun saveCustomer(customerDTO: Mono<CustomerDTO>): Mono<CustomerDTO> {
        return customerDTO.map { it.toCustomer() }.flatMap { customerRepository.save(it) }.map { it.toCustomerDto() }
    }

    override fun saveCustomer(customerDTO: CustomerDTO): Mono<CustomerDTO> {
        return customerRepository.save(customerDTO.toCustomer()).map { it.toCustomerDto() }
    }

    override fun updateCustomer(customerId: String, customerDTO: CustomerDTO): Mono<CustomerDTO> {
        return customerRepository.findById(customerId)
            .map { foundCustomer ->
                foundCustomer.customerName = customerDTO.customerName
                foundCustomer.lastModifiedDate = LocalDateTime.now()

                foundCustomer
            }.flatMap { customerRepository.save(it) }
            .map { it.toCustomerDto() }
    }

    override fun patchCustomer(customerId: String, customerDTO: CustomerDTO): Mono<CustomerDTO> {
        return customerRepository.findById(customerId)
            .map { foundCustomer ->
                if (customerDTO.customerName.isNotBlank()) {
                    foundCustomer.customerName = customerDTO.customerName
                    foundCustomer.lastModifiedDate = LocalDateTime.now()
                }
                foundCustomer
            }.flatMap { customerRepository.save(it) }
            .map { it.toCustomerDto() }
    }

    override fun deleteCustomerById(customerId: String): Mono<Void> {
        return customerRepository.deleteById(customerId)
    }

    override fun findByCustomerName(customerName: String): Flux<CustomerDTO> {
        return customerRepository.findByCustomerName(customerName).map { it.toCustomerDto() }
    }
}