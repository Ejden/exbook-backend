package pl.exbook.exbook.category.domain

import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.NotFoundException

class CategoryNotFoundException(categoryId: CategoryId) : NotFoundException("Category ${categoryId.raw} not found")
