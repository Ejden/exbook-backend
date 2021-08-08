package pl.exbook.exbook.features

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.assertions.CategoriesDtoAssertions
import pl.exbook.exbook.category.domain.Image
import pl.exbook.exbook.shared.TestData

import static pl.exbook.exbook.builders.CategoryBuilder.aCategoryBuilder

class CategoryFeatureSpec extends BaseFeatureE2ESpec {

    def "should get all categories"() {
        given:
            thereIsCategory(aCategoryBuilder()
                .withId(TestData.CATEGORY_ID_1)
                .withName(TestData.CATEGORY_NAME_1)
                .withImage(new Image(TestData.IMAGE_URL_1)))

            thereIsCategory(aCategoryBuilder()
                    .withId(TestData.CATEGORY_ID_2)
                    .withName(TestData.CATEGORY_NAME_2)
                    .withImage(new Image(TestData.IMAGE_URL_2)))

            thereIsCategory(aCategoryBuilder()
                    .withId(TestData.CATEGORY_ID_3)
                    .withName(TestData.CATEGORY_NAME_3)
                    .withParentId(TestData.CATEGORY_NAME_2)
                    .withImage(new Image(TestData.IMAGE_URL_3)))

        when:
            ResponseEntity response = getAllCategories()

        then:
            response.statusCode == HttpStatus.OK

        and:
            CategoriesDtoAssertions.assertThat(response.body)
            print(response)
    }

    def "should return 401 when adding new category without permission"() {

    }
}
