package guru.springframework.spring6reactivemongo.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDateTime

data class BeerDTO(
    var id: String = "",

    @field:NotBlank
    @field:Size(min = 3, max = 255)
    var beerName: String = "",

    @field:Size(min = 1, max = 255)
    var beerStyle: String = "",

    @field:Size(max = 25)
    var upc: String = "",
    var quantityOnHand: Int = 0,
    var price: BigDecimal = BigDecimal(0),
    var createdDate: LocalDateTime = LocalDateTime.now(),
    var lastModifiedDate: LocalDateTime = LocalDateTime.now(),
)