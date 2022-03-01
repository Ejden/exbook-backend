package pl.exbook.exbook.features

import org.springframework.boot.test.web.client.TestRestTemplate
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.CreateCategoryCommand

class CategoryEndpointSpec(private val categoryFacade: CategoryFacade, private val rest: TestRestTemplate) : BaseIntegrationSpec({
    should("do something") {
        categoryFacade.addCategory(CreateCategoryCommand(
            name = "new-category",
            parentId = null
        ))
        val categories = rest.getForEntity("/api/categories", Any::class.java)

        println(categories)
    }
})

//internal class CategoryEndpointSpec: BaseFeatureE2ESpec() {

//    @Test
//    fun `should get all categories`() {
//        // given
//        thereIsCategory(aCategoryBuilder()
//            .withId(CATEGORY_ID_1)
//            .withName(CATEGORY_NAME_1)
//            .withImage(Image(IMAGE_URL_1)))
//
//        thereIsCategory(aCategoryBuilder()
//                .withId(CATEGORY_ID_2)
//                .withName(CATEGORY_NAME_2)
//                .withImage(Image(IMAGE_URL_2)))
//
//        thereIsCategory(aCategoryBuilder()
//                .withId(CATEGORY_ID_3)
//                .withName(CATEGORY_NAME_3)
//                .withParentId(CATEGORY_ID_2)
//                .withImage(Image(IMAGE_URL_3)))
//
//        // when
//        val response = getAllCategories()
//
//        // then
//        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
//
//        // and
//        assertThatCategories()
//            .hasCategories(
//                CategoryDto(CATEGORY_ID_1, CATEGORY_NAME_1, ImageDto(IMAGE_URL_1), null),
//                CategoryDto(CATEGORY_ID_2, CATEGORY_NAME_2, ImageDto(IMAGE_URL_2), null),
//                CategoryDto(CATEGORY_ID_3, CATEGORY_NAME_3, ImageDto(IMAGE_URL_3), CATEGORY_ID_2)
//            )
//    }
//
//    @Test
//    fun `should return 401 when adding new category without permission`() {
//        // given
//        val newCategory = NewCategory(CATEGORY_NAME_1, null)
//
//        // when
//        val response = addNewCategory(newCategory)
//
//        // then
//        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
//
//        // and
//            assertThatCategories()
//                .hasNoCategories()
//    }
//
//    @Test
//    fun `should add new category with admin privileges`() {
//        // given
//        thereIsUser(aUserBuilder()
//                .withLogin("ADMIN")
//                .withPassword(BCryptPasswordEncoder().encode("ADMIN_PASSWORD"))
//                .withAdminPrivileges()
//                .withActiveAccount()
//        )
//        val credentials = LoginCredentials("ADMIN", "ADMIN_PASSWORD")
//        val newCategory = NewCategory(CATEGORY_NAME_1, null)
//
//        // when
//        val response = addNewCategoryWithCredentials(newCategory, credentials)
//
//        // then
//        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
//    }
//}
