# Contractor Plugin

Download, clone or copy OpenAPI contract to project and generate REST interfaces. The plugin is preferred for Spring projects.

## Usage

Add plugin with clause:

### Kotlin

*build.gradle.kts*

```
plugins {
  id("com.miquido.contractor-plugin") version "1.1.4"
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
    id 'com.miquido.contractor-plugin' version '1.1.4'
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

Plugin will automatically generate Kotlin/Java interfaces based on configuration.

## Configuration

Plugin needs additional configuration to generate classes.

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
                apiGenerationTargetPackagePath = "org.example",
                mainSpecificationFilePath = "bank/clients/v1/spec.yaml",
                additionalSpecificationFilePaths = mapOf(
                    "bank/common/v1" to listOf("common.yaml"),
                    "bank/api/v1" to listOf("firstSpec.yaml", "secondSpec.yaml")
                )
            ),
            GitlabAccessTokenAcquireStrategy.Configuration(
               projectId = "123456",
               accessToken = System.getenv("GITLAB_ACCESS_TOKEN"),
               baseUrl = "https://gitlab.com",
               branch = "main"
            )
        ),
        LocalConfigurationAcquireStrategy(
            BaseStrategyConfiguration(
                apiGenerationTargetPackagePath = "org.example",
                mainSpecificationFilePath = "bank/clients/v1/spec.yaml",
                additionalSpecificationFilePaths = mapOf(
                    "bank/common/v1" to listOf("common.yaml"),
                    "bank/api/v1" to listOf("firstSpec.yaml", "secondSpec.yaml")
                )
            ),
            LocalConfigurationAcquireStrategy.Configuration(
                relativePath = ".../example-project"
            )
        ),
        GitCloneAcquireStrategy(
            BaseStrategyConfiguration(
                apiGenerationTargetPackagePath = "org.example",
                mainSpecificationFilePath = "bank/clients/v1/spec.yaml",
                additionalSpecificationFilePaths = mapOf(
                    "bank/common/v1" to listOf("common.yaml"),
                    "bank/api/v1" to listOf("firstSpec.yaml", "secondSpec.yaml")
                )
            ),
            GitCloneAcquireStrategy.Configuration(
                gitCloneUrl = "git@gitlab.com:company/example/example-project.git",
                repositoryName = "example-project",
                branchName = "main"
            )
        ),
        FallbackAcquireStrategy(
            BaseStrategyConfiguration(
                apiGenerationTargetPackagePath = "org.example",
                mainSpecificationFilePath = "bank/clients/v1/spec.yaml",
                additionalSpecificationFilePaths = mapOf(
                    "bank/common/v1" to listOf("common.yaml"),
                    "bank/api/v1" to listOf("firstSpec.yaml", "secondSpec.yaml")
                )
            ),
            FallbackAcquireStrategy.Configuration(
                strategyConfigurations = listOf(
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
    )
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
                "org.example", // apiGenerationTargetPackagePath
                "bank/clients/v1/spec.yaml", // mainSpecificationFilePath
                [
                    "bank/common/v1": ["common.yaml"],
                    "bank/api/v1": ["firstSpec.yaml", "secondSpec.yaml"],
                ]
            ),
            new GitlabAccessTokenAcquireStrategy.Configuration(
                "123456", // projectId
                System.getenv("GITLAB_ACCESS_TOKEN"), // accessToken
                "https://gitlab.com", // baseUrl
                "main" // branch
            )
        ),
        new LocalConfigurationAcquireStrategy(
            new BaseStrategyConfiguration(
                "org.example", // apiGenerationTargetPackagePath
                "bank/clients/v1/spec.yaml", // mainSpecificationFilePath
                [
                    "bank/common/v1": ["common.yaml"],
                    "bank/api/v1": ["firstSpec.yaml", "secondSpec.yaml"],
                ]
            ),
            new LocalConfigurationAcquireStrategy.Configuration(
                ".../example-project" // relativePath
            )
        ),
        new GitCloneAcquireStrategy(
            new BaseStrategyConfiguration(
                "org.example", // apiGenerationTargetPackagePath
                "bank/clients/v1/spec.yaml", // mainSpecificationFilePath
                [
                    "bank/common/v1": ["common.yaml"],
                    "bank/api/v1": ["firstSpec.yaml", "secondSpec.yaml"],
                ]
            ),
            new GitCloneAcquireStrategy.Configuration(
                "git@gitlab.com:company/example/example-project.git", // gitCloneUrl
                "example-project", // repositoryName
                "main" // branchName
            )
        ),
        new FallbackAcquireStrategy(
            new BaseStrategyConfiguration(
                "org.example", // apiGenerationTargetPackagePath
                "bank/clients/v1/spec.yaml", // mainSpecificationFilePath
                [
                    "bank/common/v1": ["common.yaml"],
                    "bank/api/v1": ["firstSpec.yaml", "secondSpec.yaml"],
                ]
            ),
            new FallbackAcquireStrategy.Configuration(
                [ // strategyConfigurations
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


# Parameters:

| Parameter        | Description                                                                                                                                                                                                                                                   |
|------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| contracts        | Specified by [Strategy classes](#Strategy-classes). Every defined contract creates specification classes.                                                                                                                                                     |
| skipValidateSpec | Whether or not to skip validating the input spec prior to generation. By default, invalid specifications will result in an error. For more see https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator-maven-plugin/README.md |
| generatorName    | Generator name for OpenAPI plugin configuration. Available generators: https://openapi-generator.tech/docs/generators/#server-generators                                                                                                                      |
| importMappings   | Custom types mapping configuration. For more, see https://openapi-generator.tech/docs/usage/#type-mappings-and-import-mappings                                                                                                                                |
| typeMappings     | Custom types mapping configuration. For more, see https://openapi-generator.tech/docs/usage/#type-mappings-and-import-mappings                                                                                                                                |
| configOptions    | Overrides default OpenAPI plugin configuration. Both can be found at https://openapi-generator.tech/docs/generators/kotlin/ and https://openapi-generator.tech/docs/generators/kotlin-spring/.                                                                |



# Classes

## Strategy classes

| Class name                         | Description                                                                                                                                                |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| LocalConfigurationAcquireStrategy  | Used for retrieving API contract files from [SOURCE PROJECT](#Legend) placed in local directory                                                            |
| GitlabAccessTokenAcquireStrategy   | Used for retrieving API contract files from [SOURCE PROJECT](#Legend) placed in gitlab repository, by downloading it using an access token                 |
| GitCloneAcquireStrategy            | Used for retrieving API contract files from [SOURCE PROJECT](#Legend) placed in any git repository, by cloning it using local git settings                 |
| FallbackAcquireStrategy            | Used for retrieving API contract files from [SOURCE PROJECT](#Legend) using the first encountered strategy from the given list that is capable of doing so |

## Configuration classes

| Class name                                                                                          | Description                                                                                                            |
|-----------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| [BaseStrategyConfiguration](#BaseStrategyConfiguration)                                             | Main configuration that is used in every strategy                                                                      |
| [LocalConfigurationAcquireStrategy.Configuration](#LocalConfigurationAcquireStrategy-Configuration) | Additional configuration that is used by `LocalConfigurationAcquireStrategy`                                           |
| [GitlabAccessTokenAcquireStrategy.Configuration](#GitlabAccessTokenAcquireStrategy-Configuration)   | Additional configuration that is used by `GitlabAccessTokenAcquireStrategy`                                            |
| [GitCloneAcquireStrategy.Configuration](#GitCloneAcquireStrategy-Configuration)                     | Additional configuration that is used by `GitCloneAcquireStrategy`                                                     |
| [FallbackAcquireStrategy.Configuration](#FallbackAcquireStrategy-Configuration)                     | Additional configuration that is used by `FallbackAcquireStrategy`                                                     |


### ~~SingleFile~~ (deprecated: use constructors with Strings):

| Attribute     | Description                                                                                    |
|---------------|------------------------------------------------------------------------------------------------|
| directoryList | Directory path list in [SOURCE PROJECT](#Legend) where api specification file should be looked |
| fileFullName  | Api specification file full name placed in `directoryList`                                     |


### ~~MultipleFiles~~ (deprecated: use constructors with Maps):

| Attribute     | Description                                                                                     |
|---------------|-------------------------------------------------------------------------------------------------|
| directoryList | Directory path list in [SOURCE PROJECT](#Legend) where api specification files should be looked |
| fileFullNames | List of api specification file full names placed in `directoryList`                             |


### BaseStrategyConfiguration:

| Attribute                        | Description                                                                                                                                                                                                                                                 |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| apiGenerationTargetPackagePath   | Base package path list in [TARGET PROJECT](#Legend) where generated api should be generated                                                                                                                                                                 |
| mainSpecificationFilePath        | Main API contract file path and name [SingleFile](#SingleFile)                                                                                                                                                                                              |
| additionalSpecificationFilePaths | OPTIONAL FIELD. List of additional API contract files names and paths [MultipleFiles](#MultipleFiles). It should be ordered relative to the references(`$ref`) inside these files. If not introduced, plugin will try to detect and acquire necessary files |


### LocalConfigurationAcquireStrategy Configuration:

| Attribute                  | Description                                                                |
|----------------------------|----------------------------------------------------------------------------|
| baseConfiguration          | Base configuration [BaseStrategyConfiguration](#BaseStrategyConfiguration) |
| configuration.relativePath | Path of locally stored API contract [SOURCE PROJECT](#Legend)              |


### GitlabAccessTokenAcquireStrategy Configuration:

| Attribute                 | Description                                                                |
|---------------------------|----------------------------------------------------------------------------|
| baseConfiguration         | Base configuration [BaseStrategyConfiguration](#BaseStrategyConfiguration) |
| configuration.projectId   | Gitlab project id                                                          |
| configuration.accessToken | Gitlab access token                                                        |
| configuration.baseUrl     | Gitlab base url of [SOURCE PROJECT](#Legend) (default: https://gitlab.com) |
| configuration.branch      | Branch of the project repository from which the file is to be downloaded   |


### GitCloneAcquireStrategy Configuration:

| Attribute                    | Description                                                                |
|------------------------------|----------------------------------------------------------------------------|
| baseConfiguration            | Base configuration [BaseStrategyConfiguration](#BaseStrategyConfiguration) |
| configuration.gitCloneUrl    | Git url for cloning                                                        |
| configuration.repositoryName | Name of [SOURCE PROJECT](#Legend) repository                               |
| configuration.branchName     | Branch of the project repository from which the file is to be cloned       |


### FallbackAcquireStrategy Configuration:

| Attribute                            | Description                                                                                                               |
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| baseConfiguration                    | Base configuration [BaseStrategyConfiguration](#BaseStrategyConfiguration)                                                |
| configuration.strategyConfigurations | A list of strategies configurations that defines the order of checking which of them can be used to obtain a API contract |


# Legend

| Attribute      | Description                                      |
|----------------|--------------------------------------------------|
| SOURCE PROJECT | Project where api specification files are stored |
| TARGET PROJECT | Project where api classes will be generated      |

# FAQ

1. What do this plugin use to generate API files?
> [Openapi generator plugin](https://openapi-generator.tech/docs/installation/)

2. How can I generate separate API interfaces files in My [TARGET PROJECT](#Legend) for each endpoint?
> API interfaces files are generated based on [tags](https://swagger.io/docs/specification/grouping-operations-with-tags/) parameter declared on each path element(if [useTags](#Parameters) configuration is enabled).
> If two or more path have the same tag, then all of them will be placed in the same file.

3. In generated API methods names include name of their API, what can I do to remove it? What can I do, to declare my own names of methods?
> API methods names can be declared with parameter [operationId](https://swagger.io/docs/specification/paths-and-operations/) parameter declared on each path element.
