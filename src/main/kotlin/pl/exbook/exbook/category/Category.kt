package pl.exbook.exbook.category

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "categories")
class Category(val name: String?) {
    var id: String? = null
    var svgImg: String? = null
    var subcategories: MutableList<Category> = mutableListOf()

    constructor(id: String, name: String?, svgImg: String?) : this(name) {
        this.id = id
        this.svgImg = svgImg
    }
}