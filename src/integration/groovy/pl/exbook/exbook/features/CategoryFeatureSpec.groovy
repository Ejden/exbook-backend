package pl.exbook.exbook.features

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.category.adapter.rest.CategoryDto
import pl.exbook.exbook.category.adapter.rest.ImageDto
import pl.exbook.exbook.category.adapter.rest.NewCategory
import pl.exbook.exbook.category.domain.Image

import static pl.exbook.exbook.builders.CategoryBuilder.aCategoryBuilder
import static pl.exbook.exbook.shared.TestData.*

class CategoryFeatureSpec extends BaseFeatureE2ESpec {

    def "should get all categories"() {
        given:
            thereIsCategory(aCategoryBuilder()
                .withId(CATEGORY_ID_1)
                .withName(CATEGORY_NAME_1)
                .withImage(new Image(IMAGE_URL_1)))

            thereIsCategory(aCategoryBuilder()
                    .withId(CATEGORY_ID_2)
                    .withName(CATEGORY_NAME_2)
                    .withImage(new Image(IMAGE_URL_2)))

            thereIsCategory(aCategoryBuilder()
                    .withId(CATEGORY_ID_3)
                    .withName(CATEGORY_NAME_3)
                    .withParentId(CATEGORY_ID_2)
                    .withImage(new Image(IMAGE_URL_3)))

        when:
            ResponseEntity response = getAllCategories()

        then:
            response.statusCode == HttpStatus.OK

        and:
            assertThatCategories()
                .hasCategories([
                        new CategoryDto(CATEGORY_ID_1, CATEGORY_NAME_1, new ImageDto(IMAGE_URL_1), null),
                        new CategoryDto(CATEGORY_ID_2, CATEGORY_NAME_2, new ImageDto(IMAGE_URL_2), null),
                        new CategoryDto(CATEGORY_ID_3, CATEGORY_NAME_3, new ImageDto(IMAGE_URL_3), CATEGORY_ID_2)
                ])

    }

    def "should return 401 when adding new category without permission"() {
        given:
            NewCategory newCategory = new NewCategory(CATEGORY_NAME_1, null)

        when:
            ResponseEntity response = addNewCategory(newCategory)

        then:
            response.statusCode == HttpStatus.UNAUTHORIZED

        and:
            assertThatCategories()
                .hasNoCategories()
    }
}
