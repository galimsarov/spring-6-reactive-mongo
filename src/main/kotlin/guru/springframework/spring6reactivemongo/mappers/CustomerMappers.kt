package guru.springframework.spring6reactivemongo.mappers

import guru.springframework.spring6reactivemongo.domain.Customer
import guru.springframework.spring6reactivemongo.model.CustomerDTO

fun Customer.toCustomerDto(): CustomerDTO = CustomerDTO(
    id = id,
    customerName = customerName,
    createdDate = createdDate,
    lastModifiedDate = lastModifiedDate
)

fun CustomerDTO.toCustomer(): Customer = Customer(
    id = id,
    customerName = customerName,
    createdDate = createdDate,
    lastModifiedDate = lastModifiedDate
)