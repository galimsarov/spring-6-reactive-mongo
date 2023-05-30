package guru.springframework.spring6reactivemongo.repositories

import guru.springframework.spring6reactivemongo.domain.Beer
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface BeerRepository : ReactiveMongoRepository<Beer, String>