package pl.exbook.exbook.util.retrofit

import mu.KLogging
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.filter.AnnotationTypeFilter

class RetrofitServiceRegistrar : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val scanner = getScanner().addRetrofitAnnotationFilter()
        val candidates = scanner.findCandidateComponents("pl.exbook.exbook")
        candidates.forEach { service ->
            try {
                val serviceClass = Class.forName(service.beanClassName)

                createRetrofitServiceBean(registry, serviceClass)
            } catch (caused: Exception) {
                logger.error { "Couldn't create service ${service.beanClassName}" }
                throw caused
            }
        }
    }

    private fun getScanner() = object : ClassPathScanningCandidateComponentProvider(false) {
        override fun isCandidateComponent(beanDefinition: AnnotatedBeanDefinition): Boolean {
            return super.isCandidateComponent(beanDefinition) || beanDefinition.metadata.isAbstract
        }
    }

    private fun ClassPathScanningCandidateComponentProvider.addRetrofitAnnotationFilter() = this
        .apply { addIncludeFilter(AnnotationTypeFilter(RetrofitService::class.java)) }

    private fun <T> createRetrofitServiceBean(registry: BeanDefinitionRegistry, serviceClass: Class<T>) {
        val beanDefinition = GenericBeanDefinition().apply {
            setBeanClass(serviceClass)
            factoryBeanName = "RetrofitServiceFactory"
            factoryMethodName = "createClient"
            constructorArgumentValues = ConstructorArgumentValues().apply {
                addGenericArgumentValue(serviceClass)
            }
            setDependsOn("RetrofitServiceFactory")
        }

        registry.registerBeanDefinition(serviceClass.name, beanDefinition)
    }

    companion object : KLogging()
}
