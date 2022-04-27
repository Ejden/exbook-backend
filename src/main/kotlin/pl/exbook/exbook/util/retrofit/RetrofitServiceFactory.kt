package pl.exbook.exbook.util.retrofit

import mu.KLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@EnableConfigurationProperties(RetrofitServicesConfiguration::class)
@Component("RetrofitServiceFactory")
class RetrofitServiceFactory(
    private val servicesConfigurations: RetrofitServicesConfiguration
) {
    fun <T> createClient(serviceClass: Class<T>): T {
        val annotation = serviceClass.getAnnotation(RetrofitService::class.java)
        try {
            val serviceConfiguration = servicesConfigurations.services!![annotation.serviceName]!!
            logger.debug { "Creating client for ${annotation.serviceName}" }
            return createRetrofitInstance(serviceConfiguration).create(serviceClass)
        } catch (cause: NullPointerException) {
            logger.error { "Couldn't find configuration for retrofit service named: ${annotation.serviceName}" }
            throw cause
        }
    }

    private fun createRetrofitInstance(serviceConfiguration: ServiceConfiguration) = Retrofit.Builder()
        .baseUrl(serviceConfiguration.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object : KLogging()
}
