package pl.exbook.exbook.category

import org.springframework.stereotype.Service

@Service
class CategoryService(val categoryRepository: CategoryRepository) {

    fun getAllCategories() : Collection<Category> {
        val categories = categoryRepository.findAll()
        return buildCategoryTree(categories)
    }

    private fun buildCategoryTree(categories: MutableList<CategoryDatabaseModel>): Collection<Category> {
        val treeStructuredCategories = arrayListOf<Category>()
        // Stores temp references to all categories to ease access them when adding subcategories
        val addedCategories = arrayListOf<Category>()

        // Mapping all database model categories to categories and storing them in tree fashion, the main categories
        // does not have parentId. Categories with parent id are added to the parent list of children
        while (categories.isNotEmpty()) {
            val i = categories.iterator()

            while (i.hasNext()) {
                val category = i.next()

                if (category.parentId == null) {
                    // This category is main category, so we're adding it on top
                    val newCategory = category.toCategory()
                    treeStructuredCategories.add(newCategory)
                    addedCategories.add(newCategory)
                    i.remove()
                } else {
                    // This category is subcategory, so we're searching for his parent and adding it to him
                    val parent = addedCategories.find { par ->  par.id.equals(category.parentId)}
                    if (parent != null) {
                        val newCategory = category.toCategory()
                        parent.subcategories.add(newCategory)
                        addedCategories.add(newCategory)
                        i.remove()
                    }
                }
            }
        }

        return treeStructuredCategories
    }

    fun addCategory(newCategoryRequest: NewCategoryRequest): Category? {
        return null
//        return categoryRepository.insert()
    }
}