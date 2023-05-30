package guru.springframework.spring6reactivemongo.model

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CustomerDTO(
    var id: String = "",

    @field:NotBlank
    var customerName: String = "",
    var createdDate: LocalDateTime = LocalDateTime.now(),
    var lastModifiedDate: LocalDateTime = LocalDateTime.now(),
)