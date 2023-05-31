package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.domain.Beer
import guru.springframework.spring6reactivemongo.mappers.toBeerDto
import guru.springframework.spring6reactivemongo.model.BeerDTO
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@SpringBootTest
class BeerServiceImplTest {
    @Autowired
    private lateinit var beerService: BeerService

    private lateinit var beerDTO: BeerDTO

    @BeforeEach
    fun setUp() {
        beerDTO = getTestBeer().toBeerDto()
    }

    @DisplayName("Test Save Beer Using Subscriber")
    @Test
    fun saveBeerUseSubscriber() {
        val atomicBoolean = AtomicBoolean(false)
        val atomicDto = AtomicReference<BeerDTO>()

        val savedMono = beerService.saveBeer(Mono.just(beerDTO))

        savedMono.subscribe { savedDto ->
            println(savedDto.id)
            atomicBoolean.set(true)
            atomicDto.set(savedDto)
        }

        Awaitility.await().untilTrue(atomicBoolean)

        val persistedDto = atomicDto.get()
        assert(persistedDto.beerName.isNotBlank())
        assert(persistedDto.id != getTestBeerDto().id)
    }

    @DisplayName("Test Save Beer Using Block")
    @Test
    fun saveBeerUseBlock() {
        val savedDto = beerService.saveBeer(Mono.just(getTestBeerDto())).block()
        assert(savedDto?.beerName?.isNotBlank() ?: false)
        assert(savedDto?.id != getTestBeerDto().id)
    }

    @DisplayName("Test Update Beer Using Block")
    @Test
    fun testUpdateBlocking() {
        val newName = "New Beer Name"
        val savedBeerDTO = getSavedBeerDto().apply { beerName = newName }

        val updatedDto = beerService.saveBeer(Mono.just(savedBeerDTO)).block()

        val fetchedDto = beerService.getById(updatedDto?.id ?: "").block()
        assert(fetchedDto?.beerName == newName)
    }

    @DisplayName("Test Update Using Reactive Streams")
    @Test
    fun testUpdateStreaming() {
        val newName = "New Beer Name"
        val atomicDto = AtomicReference<BeerDTO>()

        beerService.saveBeer(Mono.just(getTestBeerDto()))
            .map { savedBeerDto ->
                savedBeerDto.beerName = newName
                savedBeerDto
            }
            .flatMap { beerService.saveBeer(it) }
            .flatMap { beerService.getById(it.id) }
            .subscribe { atomicDto.set(it) }

        Awaitility.await().until { atomicDto.get().beerName.isNotBlank() }
        assert(atomicDto.get().beerName == newName)
    }

    @Test
    fun testDeleteBeer() {
        val beerToDelete = getSavedBeerDto()

        beerService.deleteBeerById(beerToDelete.id).block()

        val expectedEmptyBeerMono = beerService.getById(beerToDelete.id)

        val emptyBeer = expectedEmptyBeerMono.block()

        assert(emptyBeer == null)
    }

    @Test
    fun findFirstByBeerNameTest() {
        val beerDTO = getSavedBeerDto()

        val atomicBoolean = AtomicBoolean(false)
        val foundDTO: Mono<BeerDTO> = beerService.findFirstByBeerName(beerDTO.beerName)

        foundDTO.subscribe {
            println(it.toString())
            atomicBoolean.set(true)
        }

        Awaitility.await().untilTrue(atomicBoolean)
    }

    fun getSavedBeerDto(): BeerDTO {
        return beerService.saveBeer(Mono.just(getTestBeerDto())).block() ?: BeerDTO()
    }

    @Test
    fun testFindByBeerStyle() {
        val beerDTO1 = getSavedBeerDto()
        val atomicBoolean = AtomicBoolean(false)
        beerService.findByBeerStyle(beerDTO1.beerStyle).subscribe {
            println(it.toString())
            atomicBoolean.set(true)
        }

        Awaitility.await().untilTrue(atomicBoolean)
    }

    companion object {
        fun getTestBeerDto() = getTestBeer().toBeerDto()

        fun getTestBeer(): Beer {
            return Beer(
                beerName = "Space Dust",
                beerStyle = "IPA",
                price = BigDecimal.TEN,
                quantityOnHand = 12,
                upc = "123213",
            )
        }
    }
}