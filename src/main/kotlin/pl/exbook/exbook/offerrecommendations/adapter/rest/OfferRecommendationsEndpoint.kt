package pl.exbook.exbook.offerrecommendations.adapter.rest

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.offerrecommendations.OfferRecommendationsFacade
import pl.exbook.exbook.offerrecommendations.adapter.rest.dto.OfferRecommendationMapper
import pl.exbook.exbook.offerrecommendations.adapter.rest.dto.OfferRecommendationsDto
import pl.exbook.exbook.util.callhandler.handleRequest

@RestController
@RequestMapping("api")
class OfferRecommendationsEndpoint(private val offerRecommendationsFacade: OfferRecommendationsFacade) {
    @GetMapping("recommendations/offers")
    fun getGeneralRecommendations(): ResponseEntity<OfferRecommendationsDto> = handleRequest(
        mapper = OfferRecommendationMapper,
        call = { offerRecommendationsFacade.getGeneralRecommendedOffers() },
        response = { ok(it) }
    )
}
