package guru.springframework.spring6reactivemongo.domain

import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Customer(
    @Id
    var id: String = "",

    @field:Size(max = 255)
    var customerName: String = "",
    var createdDate: LocalDateTime = LocalDateTime.now(),
    var lastModifiedDate: LocalDateTime = LocalDateTime.now(),
)