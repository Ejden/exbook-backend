package pl.exbook.exbook.datamodel

import org.springframework.data.annotation.Id

class Book (
    @Id
    var id: String?,
    var author: String,
    var title: String,
    var ISBN: String,
    var description: String,
    var condition: Condition,
) {

}