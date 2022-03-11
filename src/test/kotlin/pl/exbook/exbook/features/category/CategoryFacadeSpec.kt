package pl.exbook.exbook.features.category

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.CategoryDomainAbility
import pl.exbook.exbook.category.domain.CategoryNotFoundException
import pl.exbook.exbook.category.domain.CreateCategoryCommand
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.TestData.otherSampleCategoryName
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleCategoryName
import pl.exbook.exbook.shared.ValidationException

class CategoryFacadeSpec : ShouldSpec({
    val domain = CategoryDomainAbility()

    should("create new category") {
        // given
        val command = CreateCategoryCommand(
            name = sampleCategoryName,
            parentId = null
        )

        // when
        val result = domain.facade.addCategory(command)
        val category = domain.facade.getCategory(result.id)

        // then
        category.id shouldBe result.id
        category.name shouldBe sampleCategoryName
        category.parentId.shouldBeNull()
        category.image.shouldBeNull()
    }

    should("create child category") {
        // given
        val parent = domain.thereIsCategory(name = sampleCategoryName)

        val command = CreateCategoryCommand(
            name = otherSampleCategoryName,
            parentId = parent.id
        )

        // when
        val result = domain.facade.addCategory(command)
        val category = domain.facade.getCategory(result.id)

        // then
        category.id shouldBe result.id
        category.parentId shouldBe parent.id
        category.name shouldBe otherSampleCategoryName
        category.image.shouldBeNull()
    }

    should("throw an error when trying to create category with existing name") {
        // given
        domain.thereIsCategory(name = sampleCategoryName)

        val command = CreateCategoryCommand(
            name = sampleCategoryName,
            parentId = null
        )

        // then
        shouldThrowExactly<ValidationException> {
            domain.facade.addCategory(command)
        }
    }

    should("throw an error when trying to create category without name") {
        // expect
        shouldThrowExactly<IllegalParameterException> {
            domain.facade.addCategory(
                CreateCategoryCommand(
                    name = "",
                    parentId = null
                )
            )
        }
    }

    should("throw an error when trying to create category with non existing parent category") {
        // given
        val command = CreateCategoryCommand(
            name = sampleCategoryName,
            parentId = sampleCategoryId
        )

        // then
        shouldThrowExactly<CategoryNotFoundException> {
            domain.facade.addCategory(command)
        }
    }

    should("get all categories") {
        // given
        val cat1 = domain.thereIsCategory(name = "category-1")
        domain.thereIsCategory(name = "category-2")
        domain.thereIsCategory(name = "category-3", parentId = cat1.id)

        // when
        val categories = domain.facade.getAllCategories()

        // then
        categories shouldHaveSize 3

        // and
        categories[0].name shouldBe "category-1"
        categories[0].parentId.shouldBeNull()
        categories[1].name shouldBe "category-2"
        categories[1].parentId.shouldBeNull()
        categories[2].name shouldBe "category-3"
        categories[2].parentId shouldBe categories[0].id
    }

    should("get categories tree") {
        // given
        val cat1 = domain.thereIsCategory(name = "category-1", parentId = null)
        val cat2 = domain.thereIsCategory(name = "category-2", parentId = null)
        val cat3 = domain.thereIsCategory(name = "category-3", parentId = cat1.id)
        domain.thereIsCategory(name = "category-4", parentId = cat2.id)
        domain.thereIsCategory(name = "category-5", parentId = null)
        domain.thereIsCategory(name = "category-6", parentId = cat3.id)
        domain.thereIsCategory(name = "category-7", parentId = cat3.id)


        // when
        val categories = domain.facade.getCategoriesTree()

        // then
        categories shouldHaveSize 3

        // and
        categories[0].name shouldBe "category-1"
        categories[0].parentId.shouldBeNull()
        categories[0].children shouldHaveSize 1
        categories[0].children[0].parentId shouldBe cat1.id
        categories[0].children[0].name shouldBe "category-3"
        categories[0].children[0].children shouldHaveSize 2
        categories[0].children[0].children[0].parentId shouldBe cat3.id
        categories[0].children[0].children[0].name shouldBe "category-6"
        categories[0].children[0].children[0].children.shouldBeEmpty()
        categories[0].children[0].children[1].parentId shouldBe cat3.id
        categories[0].children[0].children[1].name shouldBe "category-7"
        categories[0].children[0].children[1].children.shouldBeEmpty()

        categories[1].name shouldBe "category-2"
        categories[1].parentId.shouldBeNull()
        categories[1].children shouldHaveSize 1
        categories[1].children[0].parentId shouldBe cat2.id
        categories[1].children[0].name shouldBe "category-4"
        categories[1].children[0].children.shouldBeEmpty()

        categories[2].name shouldBe "category-5"
        categories[2].parentId.shouldBeNull()
        categories[2].children.shouldBeEmpty()
    }
})
