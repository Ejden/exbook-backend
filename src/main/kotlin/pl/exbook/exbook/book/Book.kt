package pl.exbook.exbook.book

import pl.exbook.exbook.user.User

class Book (
    var id: String?,
    var author: String,
    var title: String,
    var ISBN: String?,
    var description: String?,
    var condition: Condition,
    var seller: User?
) {

    fun toBookDto() : BookDto {
        return BookDto(
            id = this.id!!,
            author = this.author,
            title = this.title,
            description = this.description,
            ISBN = this.ISBN,
            condition = this.condition,
            seller = BookDto.Seller(seller?.id!!)
        )
    }

}

enum class Condition {
    NEW, PERFECT, LIGHTLY_USED, MODERATELY_USED, BAD
}
