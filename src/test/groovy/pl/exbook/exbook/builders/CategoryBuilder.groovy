package pl.exbook.exbook.builders

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryId
import pl.exbook.exbook.category.domain.Image

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CategoryBuilder {

    String id = "category-id"
    String name = "category-name"
    Image image = new Image("https://files.exbook.com/images/123")
    String parentId = null

    private CategoryBuilder() {}

    static CategoryBuilder aCategoryBuilder() {
        return new CategoryBuilder()
    }

    Category build() {
        return new Category(
                new CategoryId(id),
                name,
                image,
                (parentId == null) ? null : new CategoryId(parentId)
        )
    }
}
