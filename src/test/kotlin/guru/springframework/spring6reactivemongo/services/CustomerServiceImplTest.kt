package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.domain.Customer
import guru.springframework.spring6reactivemongo.mappers.toCustomerDto
import guru.springframework.spring6reactivemongo.model.CustomerDTO
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@SpringBootTest
class CustomerServiceImplTest {
    @Autowired
    private lateinit var customerService: CustomerService

    private lateinit var customerDTO: CustomerDTO

    @BeforeEach
    fun setUp() {
        customerDTO = getTestCustomerDto()
    }

    @DisplayName("Test Save Customer Using Subscriber")
    @Test
    fun saveCustomerUseSubscriber() {
        val atomicBoolean = AtomicBoolean(false)
        val atomicDto = AtomicReference<CustomerDTO>()

        val savedMono = customerService.saveCustomer(Mono.just(customerDTO))

        savedMono.subscribe { savedDto ->
            println(savedDto.id)
            atomicBoolean.set(true)
            atomicDto.set(savedDto)
        }

        Awaitility.await().untilTrue(atomicBoolean)

        val persistedDto = atomicDto.get()
        assert(persistedDto.customerName.isNotBlank())
        assert(persistedDto.id != getTestCustomerDto().id)
    }

    @DisplayName("Test Save Customer Using Block")
    @Test
    fun saveCustomerUseBlock() {
        val savedDto = customerService.saveCustomer(Mono.just(getTestCustomerDto())).block()
        assert(savedDto?.customerName?.isNotBlank() ?: false)
        assert(savedDto?.id != getTestCustomerDto().id)
    }

    @DisplayName("Test Update Customer Using Block")
    @Test
    fun testUpdateBlocking() {
        val newName = "New Customer Name"
        val savedCustomerDTO = getSavedCustomerDto().apply { customerName = newName }

        val updatedDto = customerService.saveCustomer(Mono.just(savedCustomerDTO)).block()

        val fetchedDto = customerService.getById(updatedDto?.id ?: "").block()
        assert(fetchedDto?.customerName == newName)
    }

    @DisplayName("Test Update Using Reactive Streams")
    @Test
    fun testUpdateStreaming() {
        val newName = "New Customer Name"
        val atomicDto = AtomicReference<CustomerDTO>()

        customerService.saveCustomer(Mono.just(getSavedCustomerDto()))
            .map { savedCustomerDto ->
                savedCustomerDto.customerName = newName
                savedCustomerDto
            }
            .flatMap { customerService.saveCustomer(it) }
            .flatMap { customerService.getById(it.id) }
            .subscribe { atomicDto.set(it) }

        Awaitility.await().until { atomicDto.get().customerName.isNotBlank() }
        assert(atomicDto.get().customerName == newName)
    }

    @Test
    fun testDeleteCustomer() {
        val customerToDelete = getSavedCustomerDto()

        customerService.deleteCustomerById(customerToDelete.id).block()

        val expectedEmptyCustomerMono = customerService.getById(customerToDelete.id)

        val emptyCustomer = expectedEmptyCustomerMono.block()

        assert(emptyCustomer == null)
    }

    @Test
    fun testFindByCustomerName() {
        val customerDTO1 = getSavedCustomerDto()
        val atomicBoolean = AtomicBoolean(false)
        customerService.findByCustomerName(customerDTO1.customerName).subscribe {
            println(it.toString())
            atomicBoolean.set(true)
        }

        Awaitility.await().untilTrue(atomicBoolean)
    }

    fun getSavedCustomerDto(): CustomerDTO {
        return customerService.saveCustomer(Mono.just(getTestCustomerDto())).block() ?: CustomerDTO()
    }

    companion object {
        fun getTestCustomerDto() = getTestCustomer().toCustomerDto()

        fun getTestCustomer(): Customer {
            return Customer(customerName = "Alex Ferguson")
        }
    }
}