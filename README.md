# Contractor Plugin

Download, clone or copy OpenAPI contract to project and generate REST interfaces. The plugin is preferred for Spring projects.

## Usage

Add plugin with clause:

### Kotlin

*build.gradle.kts*

```
plugins {
  id("com.miquido.contractor-plugin") version "1.1.2"
}
```

*settings.gradle.kts*

```
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // below is optional, only when mavenCentral is not updated yet
        maven {
            setUrl("https://s01.oss.sonatype.org/content/repositories/releases")
        }
    }
}
```

### Groovy

*build.gradle*

```
plugins {
    id 'com.miquido.contractor-plugin' version '1.1.2'
}
```

*settings.gradle*

```
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // below is optional, only when mavenCentral is not updated yet
        maven {
            url = "https://s01.oss.sonatype.org/content/repositories/releases"
        }
    }
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

### Kotlin

```
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.strategy.LocalConfigurationAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitlabAccessTokenAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitCloneAcquireStrategy
import com.miquido.plugin.contractor.strategy.FallbackAcquireStrategy

...

configure<ContractorConfiguration> {
    contracts = listOf(
        GitlabAccessTokenAcquireStrategy(
            BaseStrategyConfiguration(
                listOf("org", "example"),
                listOf("bank", "clients", "v1"),
                "spec.yaml",
                listOf("common.yaml", "firstDomainSpec.yaml", "secondDomainSpec.yaml")
            ),
            GitlabAccessTokenAcquireStrategy.Configuration(
                "123456",
                System.getenv("GITLAB_ACCESS_TOKEN"),
               "https://gitlab.com",
               "main"
            )
        ),
        LocalConfigurationAcquireStrategy(
            BaseStrategyConfiguration(
                listOf("org", "example"),
                listOf("bank", "clients", "v1"),
                "spec.yaml",
                listOf("common.yaml", "firstDomainSpec.yaml", "secondDomainSpec.yaml")
            ),
            LocalConfigurationAcquireStrategy.Configuration(
                ".../example-project"
            )
        ),
        GitCloneAcquireStrategy(
            BaseStrategyConfiguration(
                listOf("org", "example"),
                listOf("bank", "clients", "v1"),
                "spec.yaml",
                listOf("common.yaml", "firstDomainSpec.yaml", "secondDomainSpec.yaml")
            ),
            GitCloneAcquireStrategy.Configuration(
                "git@gitlab.com:company/example/example-project.git",
                "example-project",
                "main"
            )
        ),
        FallbackAcquireStrategy(
            BaseStrategyConfiguration(
                listOf("org", "example"),
                listOf("bank", "clients", "v1"),
                "spec.yaml",
                listOf("common.yaml", "firstDomainSpec.yaml", "secondDomainSpec.yaml")
            ),
            FallbackAcquireStrategy.Configuration(
                listOf(
                    GitlabAccessTokenAcquireStrategy.Configuration(...),
                    LocalConfigurationAcquireStrategy.Configuration(...),
                    GitCloneAcquireStrategy.Configuration(...)
                )
            )
        )
    )
    generatorName = "spring"
    importMappings = listOf(
        "StreamingResponseBody": "org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody",
        "BigInteger": "java.math.BigInteger"
    )
    typeMappings = listOf(
        "array+binary": "StreamingResponseBody",
        "string+bigint": "BigInteger"
    )
    configOptions = mapOf(
        'useTags' to 'true',
        'openApiNullable' to 'false',
        'generateConstructorWithAllArgs' to 'false',
        'generatedConstructorWithRequiredArgs' to 'false',
        'bigDecimalAsString' to 'true'
    ) // override default settings
}

```

### Groovy

```
import com.miquido.plugin.contractor.strategy.configuration.BaseStrategyConfiguration
import com.miquido.plugin.contractor.strategy.LocalConfigurationAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitlabAccessTokenAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitCloneAcquireStrategy
import com.miquido.plugin.contractor.strategy.FallbackAcquireStrategy

...

contractorPluginConfiguration {
	contracts = [
            new GitlabAccessTokenAcquireStrategy(
                new BaseStrategyConfiguration(
                    ["org", "example"],
                    ["bank", "clients", "v1"],
                    "spec.yaml",
                    ["specCommon.yaml", "specTwo.yaml", "specThree.yaml"]
                ),
                new GitlabAccessTokenAcquireStrategy.Configuration(
                    "123456",
                    System.getenv("GITLAB_ACCESS_TOKEN"),
                    "https://gitlab.com",
                    "main"
                )
            ),
            new LocalConfigurationAcquireStrategy(
                new BaseStrategyConfiguration(
                    ["org", "example"],
                    ["bank", "clients", "v1"],
                    "spec.yaml",
                    ["specCommon.yaml", "specTwo.yaml", "specThree.yaml"]
                ),
                new LocalConfigurationAcquireStrategy.Configuration(
                    ".../example-project"
                )
            ),
            new GitCloneAcquireStrategy(
                new BaseStrategyConfiguration(
                    ["org", "example"],
                    ["bank", "clients", "v1"],
                    "spec.yaml",
                    ["specCommon.yaml", "specTwo.yaml", "specThree.yaml"]
                ),
                new GitCloneAcquireStrategy.Configuration(
                    "git@gitlab.com:company/example/example-project.git",
                    "example-project",
                    "main"
                )
            ),
            new FallbackAcquireStrategy(
                new BaseStrategyConfiguration(
                    ["org", "example"],
                    ["bank", "clients", "v1"],
                    "spec.yaml",
                    ["specCommon.yaml", "specTwo.yaml", "specThree.yaml"]
                ),
                new FallbackAcquireStrategy.Configuration(
                    [
                        new GitlabAccessTokenAcquireStrategy.Configuration(...),
                        new LocalConfigurationAcquireStrategy.Configuration(...),
                        new GitCloneAcquireStrategy.Configuration(...)
                    ]
                )
            )
	]
	generatorName = "spring"
    skipValidateSpec = true
    importMappings = [
        "StreamingResponseBody": "org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody",
        "BigInteger": "java.math.BigInteger"
    ]
    typeMappings = [
        "array+binary": "StreamingResponseBody",
        "string+bigint": "BigInteger"
    ]
    configOptions = [
        useTags: 'true',
        openApiNullable: 'false',
        generateConstructorWithAllArgs: 'false',
        generatedConstructorWithRequiredArgs: 'false',
        bigDecimalAsString: 'true'
    ]
}
```


Parameter table:

| Parameter        | Description                                                                                                                                                                                                                                                   |
|------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| contracts        | Specified by class `GitlabAccessTokenAcquireStrategy`, `LocalConfigurationAcquireStrategy`, `GitCloneAcquireStrategy` or `FallbackAcquireStrategy`. Every definied contract creates specification with interfaces.                                            |
| skipValidateSpec | Whether or not to skip validating the input spec prior to generation. By default, invalid specifications will result in an error. For more see https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator-maven-plugin/README.md |
| generatorName    | Generator name for OpenAPI plugin configuration. Available generators: https://openapi-generator.tech/docs/generators/#server-generators                                                                                                                      |
| importMappings   | Custom types mapping configuration. For more, see https://openapi-generator.tech/docs/usage/#type-mappings-and-import-mappings                                                                                                                                |
| typeMappings     | Custom types mapping configuration. For more, see https://openapi-generator.tech/docs/usage/#type-mappings-and-import-mappings                                                                                                                                |
| configOptions    | Overrides default OpenAPI plugin configuration. Both can be found at https://openapi-generator.tech/docs/generators/kotlin/ and https://openapi-generator.tech/docs/generators/kotlin-spring/.                                                                |


Class table:

| Class name                         | Description                                                                                                            |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| LocalConfigurationAcquireStrategy  | Used for retrieving API contract from local directory                                                                  |
| GitlabAccessTokenAcquireStrategy   | Used for retrieving API contract from the gitlab repository by downloading it using an access token                    |
| GitCloneAcquireStrategy            | Used for retrieving API contract from any git repository by cloning it using local git settings                        |
| FallbackAcquireStrategy            | Used for retrieving API contract using the first encountered strategy from the given list that is capable of doing so  |


BaseStrategyConfiguration:

| Attribute                        | Description                                                                                                                                                                |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| generatedApiBaseDirectoryList    | Base directories list where generated api should be placed                                                                                                                 |
| specificationSourceDirectoryList | Directories list where API contract file (`specificationFileName`) should be looked. Also used for directory structure generation inside (`generatedApiBaseDirectoryList`) |
| mainSpecificationFileName        | Main API contract file name                                                                                                                                                |
| additionalSpecificationFileNames | List of additional API contract files names. It should be ordered relative to the references(`$ref`) inside these files                                                    |


LocalConfigurationAcquireStrategy.Configuration:

| Attribute                  | Description                                      |
|----------------------------|--------------------------------------------------|
| baseConfiguration          | Base configuration (`BaseStrategyConfiguration`) |
| configuration.relativePath | Path of locally stored API contract file         |


GitlabAccessTokenAcquireStrategy.Configuration:

| Attribute                 | Description                                                              |
|---------------------------|--------------------------------------------------------------------------|
| baseConfiguration         | Base configuration (`BaseStrategyConfiguration`)                         |
| configuration.projectId   | Gitlab project id                                                        |
| configuration.accessToken | Gitlab access token                                                      |
| configuration.baseUrl     | Gitlab base url (default: https://gitlab.com)                            |
| configuration.branch      | Branch of the project repository from which the file is to be downloaded |


GitCloneAcquireStrategy.Configuration:

| Attribute                    | Description                                                          |
|------------------------------|----------------------------------------------------------------------|
| baseConfiguration            | Base configuration (`BaseStrategyConfiguration`)                     |
| configuration.gitCloneUrl    | Git url for cloning                                                  |
| configuration.repositoryName | Name of project repository                                           |
| configuration.branchName     | Branch of the project repository from which the file is to be cloned |


FallbackAcquireStrategy.Configuration:

| Attribute                            | Description                                                                                                               |
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| baseConfiguration                    | Base configuration (`BaseStrategyConfiguration`)                                                                          |
| configuration.strategyConfigurations | A list of strategies configurations that defines the order of checking which of them can be used to obtain a API contract |
