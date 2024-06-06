# Contractor Plugin

Download, clone or copy OpenAPI contract to project and generate REST interfaces. The plugin is preferred for Spring projects.

## Usage

Add plugin with clause:

### Kotlin

*build.gradle.kts*

```
plugins {
  id("com.miquido.contractor-plugin") version "1.0.2"
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
    id 'com.miquido.contractor-plugin' version '1.0.2'
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
import com.miquido.plugin.contractor.configuration.ContractorConfiguration
import com.miquido.plugin.contractor.strategy.LocalConfigurationAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitlabAccessTokenAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitCloneAcquireStrategy
import com.miquido.plugin.contractor.strategy.FallbackAcquireStrategy

...

configure<ContractorConfiguration> {
    contracts = listOf(
        GitlabAccessTokenAcquireStrategy(
            listOf("org", "example"),
            listOf("bank", "clients", "v1"),
            "spec.yaml",
            "123456",
            System.getenv("GITLAB_ACCESS_TOKEN")
        ),
        LocalConfigurationAcquireStrategy(
            listOf("org", "example"),
            listOf("bank", "clients", "v1"),
            "spec.yaml",
            ".../example-project"
        ),
        GitCloneAcquireStrategy(
            listOf("org", "example"),
            listOf("bank", "clients", "v1"),
            "spec.yaml",
            "git@gitlab.com:company/example/example-project.git",
            "example-project"
        ),
        FallbackAcquireStrategy(
            listOf(
                GitlabAccessTokenAcquireStrategy(...),
                LocalConfigurationAcquireStrategy(...),
                GitCloneAcquireStrategy()
            )
        )
    )
    openApiConfiguration = mapOf("useTags" to "false") // override default settings
}

```

### Groovy

```
import com.miquido.plugin.contractor.configuration.ContractorConfiguration
import com.miquido.plugin.contractor.strategy.LocalConfigurationAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitlabAccessTokenAcquireStrategy
import com.miquido.plugin.contractor.strategy.GitCloneAcquireStrategy
import com.miquido.plugin.contractor.strategy.FallbackAcquireStrategy

...

contractorPluginConfiguration {
	contracts = [
            GitlabAccessTokenAcquireStrategy(
                ["org", "example"],
                ["bank", "clients", "v1"],
                "spec.yaml",
                "123456",
                System.getenv("GITLAB_ACCESS_TOKEN")
            ),
            LocalConfigurationAcquireStrategy(
                ["org", "example"],
                ["bank", "clients", "v1"],
                "spec.yaml",
                ".../example-project"
            ),
            GitCloneAcquireStrategy(
                ["org", "example"],
                ["bank", "clients", "v1"],
                "spec.yaml",
                "git@gitlab.com:company/example/example-project.git",
                "example-project"
            ),
            FallbackAcquireStrategy(
                [
                    GitlabAccessTokenAcquireStrategy(...),
                    LocalConfigurationAcquireStrategy(...),
                    GitCloneAcquireStrategy()
                ]
            )
	]
	openApiConfiguration = [useTags: 'false']
}
```

Parameter table:

| Parameter            | Description                                                                                                                                                                                                         |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| contracts            | Specified by class `GitlabAccessTokenAcquireStrategy`, `LocalConfigurationAcquireStrategy`, `GitCloneAcquireStrategy` or `FallbackAcquireStrategy`. Every definied contract creates specification with interfaces.  |
| openApiConfiguration | Overrides default OpenAPI plugin configuration. Both can be found at https://openapi-generator.tech/docs/generators/kotlin/ and https://openapi-generator.tech/docs/generators/kotlin-spring/.                      |

Class table:

| Class name                         | Description                                                                                                            |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| LocalConfigurationAcquireStrategy  | Used for retrieving API contract from local directory                                                                  |
| GitlabAccessTokenAcquireStrategy   | Used for retrieving API contract from the gitlab repository by downloading it using an access token                    |
| GitCloneAcquireStrategy            | Used for retrieving API contract from any git repository by cloning it using local git settings                        |
| FallbackAcquireStrategy            | Used for retrieving API contract using the first encountered strategy from the given list that is capable of doing so  |

LocalConfigurationAcquireStrategy:

| Attribute                        | Description                                                                                                                                                                |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| generatedApiBaseDirectoryList    | Base directories list where generated api should be placed                                                                                                                 |
| specificationSourceDirectoryList | Directories list where API contract file (`specificationFileName`) should be looked. Also used for directory structure generation inside (`generatedApiBaseDirectoryList`) |
| specificationFileName            | API contract file name                                                                                                                                                     |
| relativePath                     | Path of locally stored API contract file                                                                                                                                   |

GitlabAccessTokenAcquireStrategy:

| Attribute                        | Description                                                                                                                                                                |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| generatedApiBaseDirectoryList    | Base directories list where generated api should be placed                                                                                                                 |
| specificationSourceDirectoryList | Directories list where API contract file (`specificationFileName`) should be looked. Also used for directory structure generation inside (`generatedApiBaseDirectoryList`) |
| specificationFileName            | API contract file name                                                                                                                                                     |
| projectId                        | Gitlab project id                                                                                                                                                          |
| accessToken                      | Gitlab access token                                                                                                                                                        |
| baseUrl                          | Gitlab base url (default: https://gitlab.com)                                                                                                                              |
| branch                           | Branch of the project repository from which the file is to be downloaded                                                                                                   |


GitCloneAcquireStrategy:

| Attribute                        | Description                                                                                                                                                                |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| generatedApiBaseDirectoryList    | Base directories list where generated api should be placed                                                                                                                 |
| specificationSourceDirectoryList | Directories list where API contract file (`specificationFileName`) should be looked. Also used for directory structure generation inside (`generatedApiBaseDirectoryList`) |
| specificationFileName            | API contract file name                                                                                                                                                     |
| gitCloneUrl                      | Git url for cloning                                                                                                                                                        |
| repositoryName                   | Name of project repository                                                                                                                                                 |

FallbackAcquireStrategy:

| Attribute          | Description                                                                                                |
|--------------------|------------------------------------------------------------------------------------------------------------|
| fallbackStrategies | A list of strategies that defines the order of checking which of them can be used to obtain a API contract |
