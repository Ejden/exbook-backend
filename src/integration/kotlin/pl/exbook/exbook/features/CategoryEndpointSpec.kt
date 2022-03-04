package pl.exbook.exbook.features

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.ability.AuthAbility
import pl.exbook.exbook.ability.CategoryDomainAbility
import pl.exbook.exbook.category.adapter.rest.dto.CreateCategoryRequest
import pl.exbook.exbook.shared.TestData.otherSampleCategoryName
import pl.exbook.exbook.shared.TestData.sampleAdminUsername
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleCategoryName
import pl.exbook.exbook.shared.TestData.samplePassword

class CategoryEndpointSpec(private val rest: TestRestTemplate) : BaseIntegrationSpec({
    val domain = CategoryDomainAbility(rest)
    val authAbility = AuthAbility(rest)

    should("get empty list of categories") {
        // when
        val response = domain.getCategories()

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualJson """
            {
                "categories": []
            }
        """
    }

    should("get all categories as list") {
        // given
        val token = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, password = samplePassword)
        val cat = domain.thereIsCategory(name = sampleCategoryName, token = token)
        domain.thereIsCategory(name = otherSampleCategoryName, parentId = cat.body!!.id, token = token)

        // when
        val response = domain.getCategories()

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson  """
            {
                "categories": [
                    {
                        "id": "${cat.body!!.id}",
                        "name": "$sampleCategoryName",
                        "icon": null,
                        "parentId": null
                    },
                    {
                        "name": "$otherSampleCategoryName",
                        "icon": null,
                        "parentId": "${cat.body!!.id}"
                    }
                ]
            }
        """
    }

    should("get all categories as tree") {
        // given
        val token = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, password = samplePassword)
        val cat = domain.thereIsCategory(name = sampleCategoryName, token = token)
        domain.thereIsCategory(name = otherSampleCategoryName, parentId = cat.body!!.id, token = token)

        // when
        val response = domain.getCategoriesTree()

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson  """
            {
                "categories": [
                    {
                        "id": "${cat.body!!.id}",
                        "name": "$sampleCategoryName",
                        "icon": null,
                        "parentId": null,
                        "children": [
                            {
                                "name": "$otherSampleCategoryName",
                                "icon": null,
                                "parentId": "${cat.body!!.id}",
                                "children": []
                            }
                        ]
                    }
                ]
            }
        """
    }

    should("should return 401 when trying to add new category without permission") {
        // given
        val requestBody = CreateCategoryRequest(
            name = sampleCategoryName,
            parentId = null
        )

        // when
        val response = domain.addCategory(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }

    should("return 401 when trying to add new category with insufficient permissions") {
        // given
        val token = authAbility.getTokenForNewUser()
        val requestBody = CreateCategoryRequest(
            name = sampleCategoryName,
            parentId = null
        )

        // when
        val response = domain.addCategory(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }

    should("add new category with admin privileges") {
        // given
        val token = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, password = samplePassword)
        val requestBody = CreateCategoryRequest(
            name = sampleCategoryName,
            parentId = null
        )

        val response = domain.addCategory(requestBody, token)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson  """
            {
                "name": "$sampleCategoryName",
                "icon": null,
                "parentId": null
            }
        """
    }

    should("get category") {
        // given
        val token = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, password = samplePassword)
        val categoryId = domain.thereIsCategory(name = sampleCategoryName, token = token).body!!.id

        // when
        val response = domain.getCategory(categoryId)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson  """
            {
                "id": "$categoryId",
                "name": "$sampleCategoryName",
                "icon": null,
                "parentId": null
            }
        """
    }

    should("return 404 when category was not found") {
        // when
        val response = domain.getCategory(sampleCategoryId.raw)

        // then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
    }
})
