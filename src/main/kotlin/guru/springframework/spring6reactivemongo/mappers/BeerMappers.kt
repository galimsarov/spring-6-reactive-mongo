package guru.springframework.spring6reactivemongo.mappers

import guru.springframework.spring6reactivemongo.domain.Beer
import guru.springframework.spring6reactivemongo.model.BeerDTO

fun Beer.toBeerDto(): BeerDTO = BeerDTO(
    id = id,
    beerName = beerName,
    beerStyle = beerStyle,
    upc = upc,
    quantityOnHand = quantityOnHand,
    price = price,
    createdDate = createdDate,
    lastModifiedDate = lastModifiedDate,
)

fun BeerDTO.toBeer(): Beer = Beer(
    id = id,
    beerName = beerName,
    beerStyle = beerStyle,
    upc = upc,
    quantityOnHand = quantityOnHand,
    price = price,
    createdDate = createdDate,
    lastModifiedDate = lastModifiedDate,
)