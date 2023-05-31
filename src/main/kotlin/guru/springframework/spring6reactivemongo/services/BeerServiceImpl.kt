package guru.springframework.spring6reactivemongo.services

import guru.springframework.spring6reactivemongo.mappers.toBeer
import guru.springframework.spring6reactivemongo.mappers.toBeerDto
import guru.springframework.spring6reactivemongo.model.BeerDTO
import guru.springframework.spring6reactivemongo.repositories.BeerRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Suppress("unused")
class BeerServiceImpl(private val beerRepository: BeerRepository) : BeerService {
    override fun listBeers(): Flux<BeerDTO> {
        return beerRepository.findAll().map { it.toBeerDto() }
    }

    override fun saveBeer(beerDTO: Mono<BeerDTO>): Mono<BeerDTO> {
        return beerDTO.map { it.toBeer() }.flatMap { beerRepository.save(it) }.map { it.toBeerDto() }
    }

    override fun saveBeer(beerDTO: BeerDTO): Mono<BeerDTO> {
        return beerRepository.save(beerDTO.toBeer()).map { it.toBeerDto() }
    }

    override fun getById(beerId: String): Mono<BeerDTO> {
        return beerRepository.findById(beerId).map { it.toBeerDto() }
    }

    override fun updateBeer(beerId: String, beerDTO: BeerDTO): Mono<BeerDTO> {
        return beerRepository.findById(beerId)
            .map { foundBeer ->
                foundBeer.beerName = beerDTO.beerName
                foundBeer.beerStyle = beerDTO.beerStyle
                foundBeer.price = beerDTO.price
                foundBeer.upc = beerDTO.upc
                foundBeer.quantityOnHand = beerDTO.quantityOnHand
                foundBeer.lastModifiedDate = LocalDateTime.now()

                foundBeer
            }.flatMap { beerRepository.save(it) }
            .map { it.toBeerDto() }
    }

    override fun patchBeer(beerId: String, beerDTO: BeerDTO): Mono<BeerDTO> {
        return beerRepository.findById(beerId)
            .map { foundBeer ->
                var updated = false
                if (beerDTO.beerName.isNotBlank()) {
                    foundBeer.beerName = beerDTO.beerName
                    updated = true
                }
                if (beerDTO.beerStyle.isNotBlank()) {
                    foundBeer.beerStyle = beerDTO.beerStyle
                    updated = true
                }
                if (beerDTO.price != BigDecimal(0)) {
                    foundBeer.price = beerDTO.price
                    updated = true
                }
                if (beerDTO.upc.isNotBlank()) {
                    foundBeer.upc = beerDTO.upc
                    updated = true
                }
                if (beerDTO.quantityOnHand != 0) {
                    foundBeer.quantityOnHand = beerDTO.quantityOnHand
                    updated = true
                }
                if (updated) {
                    foundBeer.lastModifiedDate = LocalDateTime.now()
                }
                foundBeer
            }.flatMap { beerRepository.save(it) }
            .map { it.toBeerDto() }
    }

    override fun deleteBeerById(beerId: String): Mono<Void> {
        return beerRepository.deleteById(beerId)
    }

    override fun findFirstByBeerName(beerName: String): Mono<BeerDTO> {
        return beerRepository.findFirstByBeerName(beerName).map { it.toBeerDto() }
    }

    override fun findByBeerStyle(beerStyle: String): Flux<BeerDTO> {
        return beerRepository.findByBeerStyle(beerStyle).map { it.toBeerDto() }
    }
}