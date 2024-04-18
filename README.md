# API Interface Generator based by OpenAPI

Gradle plugin to generate API interfaces based by OpenAPI specification from local or remote directory.

## Build

Due to compile and deploy plugin to local repository, type following instruction:

`./gradlew clean publishToMavenLocal`

## Usage

Add plugin with clause:

```
plugins {
  id("interface-generator-open-api") version "1.0.0"
}
```

Plugin will automatically generate Kotlin interfaces based on configuration.

## Configuration

Plugin needs additional configuration to generate classes. You need to specify local and/or remote localization and
specify OpenAPI files based on following directory convention:

`rootDir/project/domain/version/fileName`

Where `project`, `domain`, `version` and `fileName` is configurable in lists of contracts, but the `rootDir` could
be `local` or `repository`.

Example:

```
configure<OpenApiGeneratorConfiguration> {
    contracts = listOf(
        RemoteOpenApiSpecification("spec.yaml", "org.example", "bank", "clients", "v1"),
        LocalOpenApiSpecification("spec.yaml", "org.example", "bank", "cards", "v1")
    )
    local = LocalConfiguration(relativePath = "../poc-contract-first-open-api")
    repository = GitlabConfiguration(projectId = "123123", accessToken = "gitlabToken")
    openApiConfiguration = mapOf("useTags" to "false") // override default settings
}

```

Parameter table

| Parameter            | Description                                                                                                                                                                                    |   
|----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| contracts            | Specified by class `RemoteOpenApiSpecification` or `LocalOpenApiSpecification`. Every definied contract creates specification with interfaces.                                                 |
| local                | Specifies local path to OpenAPI repository. Must be set due to `LocalOpenApiSpecification` class.                                                                                              |
| repository           | Specifies remote repository with OpenAPI files. Gitlab is currently supported repository. Must be set due to `RemoteOpenApiSpecification`.                                                     |
| openApiConfiguration | Overrides default OpenAPI plugin configuration. Both can be found at https://openapi-generator.tech/docs/generators/kotlin/ and https://openapi-generator.tech/docs/generators/kotlin-spring/. |

Class table

| Class name                 | Description                                                                                                                                                                                            |
|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| LocalOpenApiSpecification  | Contains settings with following local repository structure: ``project/domain/version/fileName``. `basePackage` is used as a prefix of generated classes package: `basePackage.project.domain.version` |
| RemoteOpenApiSpecification | Like `LocalOpenApiSpecification`, but contains additional data. `baseUrl` can be set when private gitlab repository is used. `branch` can be set when different branch than `main` is used.            |
| LocalConfiguration         | `relativePath` - relative path to project                                                                                                                                                              |
| GitlabConfiguration        | `projectId` - Gitlab project ID, `accessToken` - Gitlab access token.                                                                                                                                  |