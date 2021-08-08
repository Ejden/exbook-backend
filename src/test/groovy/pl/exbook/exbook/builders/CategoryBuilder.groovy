package pl.exbook.exbook.builders

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryId

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CategoryBuilder {

    String id = "category-id"
    String name = "category-name"

    private CategoryBuilder() {}

    static CategoryBuilder aCategoryBuilder() {
        return new CategoryBuilder()
    }

    Category build() {
        return new Category(
                id = new CategoryId(id),

        )
    }
}
