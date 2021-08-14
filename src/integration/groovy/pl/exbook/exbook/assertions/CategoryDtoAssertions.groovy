package pl.exbook.exbook.assertions

import pl.exbook.exbook.category.adapter.rest.CategoryDto
import pl.exbook.exbook.category.adapter.rest.ImageDto

class CategoryDtoAssertions {

    private Map<String, Object> category

    private CategoryDtoAssertions(Map<String, Object> category) {
        this.category = category
    }

    static assertThat(Object category) {
        assert category != null
        return new CategoryDtoAssertions(category as Map<String, Object>)
    }

    CategoryDtoAssertions hasId(String id) {
        assert category.id == id
        return this
    }

    CategoryDtoAssertions hasName(String name) {
        assert category.name == name
        return this
    }

    CategoryDtoAssertions hasIconUrl(String url) {
        assert category.icon.url == url
        return this
    }

    CategoryDtoAssertions hasParentId(String parentId) {
        assert category.parentId == parentId
        return this
    }

    CategoryDtoAssertions hasNoParentId() {
        assert category.parentId == null
        return this
    }
}

class CategoriesDtoAssertions {

    private Map<String, Object> categories

    private CategoriesDtoAssertions(Map<String, Object> categories) {
        this.categories = categories
    }

    static assertThat(Object categories) {
        assert categories != null
        return new CategoriesDtoAssertions(categories as Map<String, Object>)
    }

    CategoriesDtoAssertions hasCategories(List<CategoryDto> categories) {
        assert this.categories.collect().collect {
            new CategoryDto(it.id, it.name, new ImageDto(it.icon.url), it.parentId)
        } == categories
        return this
    }

    CategoriesDtoAssertions hasNoCategories() {
        assert this.categories.collect().size() == 0
        return this
    }
}
