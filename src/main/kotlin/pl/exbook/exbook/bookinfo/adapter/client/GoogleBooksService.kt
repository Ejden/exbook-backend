package pl.exbook.exbook.bookinfo.adapter.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.stereotype.Service
import pl.exbook.exbook.bookinfo.domain.BookIdentifier
import pl.exbook.exbook.bookinfo.domain.BookInfo
import pl.exbook.exbook.bookinfo.domain.BookInfoProvider
import pl.exbook.exbook.shared.ExternalServiceException
import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.util.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@RetrofitService("google-books")
interface GoogleInfoService {
    @GET("volumes")
    fun getBooksByIsbn(@Query("q") query: String): Call<BookInfoDto>
}

@Service
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class GoogleInfoProviderService(private val googleInfoService: GoogleInfoService) : BookInfoProvider {
    override fun getBookInformation(isbn: String): BookInfo {
        val query = "isbn:${isbn}"
        val response = googleInfoService.getBooksByIsbn(query).execute()

        if (response.isSuccessful) {
            return response.body()?.items?.firstOrNull()?.toDomain() ?: throw BookNotFoundException(isbn)
        }

        throw GoogleBooksServiceException(isbn)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class BookInfoDto(
    val items: List<Item>
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Item(
        val volumeInfo: VolumeInfo
    ) {
        fun toDomain() = BookInfo(
            title = volumeInfo.title,
            subtitle = volumeInfo.subtitle,
            authors = volumeInfo.authors,
            publishedDate = volumeInfo.publishedDate,
            identifiers = volumeInfo.industryIdentifiers.map { BookIdentifier(it.type, it.identifier) }
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class VolumeInfo(
        val title: String,
        val subtitle: String,
        val authors: List<String>,
        val publishedDate: String,
        val industryIdentifiers: List<IndustryIdentifier>
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class IndustryIdentifier(
        val type: String,
        val identifier: String
    )
}

class GoogleBooksServiceException(val isbn: String) : ExternalServiceException("Failed getting book with isbn: $isbn")
class BookNotFoundException(val isbn: String) : NotFoundException("Book not found with isbn: $isbn")
