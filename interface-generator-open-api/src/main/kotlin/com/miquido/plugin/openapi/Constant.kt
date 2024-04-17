package com.miquido.plugin.openapi


object Constant {
    const val configurationDir = "build/generated/interface-generator/plugin/configuration"
    const val specificationDir = "build/generated/interface-generator/specification"
    const val interfaceDir = "build/generated/interface-generator/api/"
    const val tempDirectoryName = "temp"

    // https://openapi-generator.tech/docs/generators/kotlin/
    // https://openapi-generator.tech/docs/generators/kotlin-spring/
    val openApiProperties: Map<String, String> = mapOf(
        "interfaceOnly" to "true", // Whether to generate only API interface stubs without the server files.
        "useSpringBoot3" to "true", // Generate code and provide dependencies for use with Spring Boot 3.x. (Use jakarta instead of javax in import)
        "serializationLibrary" to "kotlinx_serialization",
        "exceptionHandler" to "false", // generate default global exception handlers (not compatible with reactive. enabling reactive will disable exceptionHandler )
        "enumPropertyNaming" to "UPPERCASE", // Naming convention for enum properties: 'camelCase', 'PascalCase', 'snake_case', 'UPPERCASE', and 'original'
        "idea" to "true", // Add Intellij Idea plugin and mark Kotlin main and test folders as source
        "library" to "spring-boot", // library template (sub-template)
        "requestMappingMode" to "controller", // Where to generate the class level @RequestMapping annotation.
        "skipDefaultInterface" to "true", // Whether to skip generation of default implementations for java8 interfaces
        "useTags" to "true", // use tags for creating interface and controller classnames
        "useResponseEntity" to "false", // Use the ResponseEntity type to wrap return values of generated API methods. If disabled, method are annotated using a @ResponseStatus annotation, which has the status of the first response declared in the Api definition
    )
}