package pl.exbook.exbook.assertions

class CategoryDtoAssertions {

    private Map<String, Object> category

    private CategoryDtoAssertions(Map<String, Object> category) {
        this.category = category
    }

    static assertThat(Object category) {
        assert category != null
        return new CategoryDtoAssertions(category as Map<String, Object>)
    }
}

class CategoriesDtoAssertions {

    private Map<String, Object> categories

    private CategoriesDtoAssertions(Map<String, Object> category) {
        this.categories = categories
    }

    static assertThat(Object categories) {
        assert categories != null
        return new CategoriesDtoAssertions(categories as Map<String, Object>)
    }

    CategoryDtoAssertions categoryWithId(String id) {
        assert categories[id]
        return CategoryDtoAssertions.assertThat(categories[id])
    }
}
