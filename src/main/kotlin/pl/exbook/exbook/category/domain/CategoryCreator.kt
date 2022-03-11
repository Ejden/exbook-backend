package pl.exbook.exbook.category.domain

import java.util.UUID
import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.ValidationException

@Service
class CategoryCreator(private val categoryRepository: CategoryRepository) {
    fun createCategory(command: CreateCategoryCommand): Category {
        command.validate()

        val category = Category(
            id = CategoryId(UUID.randomUUID().toString()),
            name = command.name,
            image = null,
            parentId = command.parentId
        )

        return categoryRepository.save(category)
    }

    private fun CreateCategoryCommand.validate() = this
        .also {
            if(categoryRepository.findByName(this.name) != null) {
                throw ValidationException("Cannot create category with already existing name")
            }
        }
        .also {
            this.parentId?.also { id ->
                if (categoryRepository.getById(id) == null) {
                    throw CategoryNotFoundException(id)
                }
            }
        }
}
