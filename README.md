# Contractor Plugin

Download or copy OpenAPI contract to project and generate REST interfaces. The plugin is preferred for Spring projects.

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
import com.miquido.plugin.contractor.configuration.GitlabConfiguration
import com.miquido.plugin.contractor.configuration.LocalConfiguration
import com.miquido.plugin.contractor.model.LocalOpenApiSpecification
import com.miquido.plugin.contractor.model.RemoteOpenApiSpecification

...

configure<ContractorConfiguration> {
    contracts = listOf(
        RemoteOpenApiSpecification("spec.yaml", "org.example", "bank", "clients", "v1"),
        LocalOpenApiSpecification("spec.yaml", "org.example", "bank", "cards", "v1")
    )
    local = LocalConfiguration(relativePath = "../contracts")
    repository = GitlabConfiguration(projectId = "123123", accessToken = "gitlabToken")
    openApiConfiguration = mapOf("useTags" to "false") // override default settings
}

```

### Groovy

```
import com.miquido.plugin.contractor.model.LocalOpenApiSpecification
import com.miquido.plugin.contractor.model.RemoteOpenApiSpecification
import com.miquido.plugin.contractor.configuration.LocalConfiguration
import com.miquido.plugin.contractor.configuration.GitlabConfiguration

...

contractorPluginConfiguration {
	repository = new GitlabConfiguration("123123", "gitlabToken")
	local = new LocalConfiguration("../contracts")
	contracts = [
            new RemoteOpenApiSpecification("spec.yaml", "org.example", "bank", "clients", "v1", "https://gitlab.com", "main"),
            new LocalOpenApiSpecification("spec.yaml", "org.example", "bank", "cards", "v1")
	]
	openApiConfiguration = [useTags: 'false']
}
```

Parameter table:

| Parameter            | Description                                                                                                                                                                                    |
|----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| contracts            | Specified by class `RemoteOpenApiSpecification` or `LocalOpenApiSpecification`. Every definied contract creates specification with interfaces.                                                 |
| local                | Specifies local path to OpenAPI repository. Must be set due to `LocalOpenApiSpecification` class.                                                                                              |
| repository           | Specifies remote repository with OpenAPI files. Gitlab is currently supported repository. Must be set due to `RemoteOpenApiSpecification`.                                                     |
| openApiConfiguration | Overrides default OpenAPI plugin configuration. Both can be found at https://openapi-generator.tech/docs/generators/kotlin/ and https://openapi-generator.tech/docs/generators/kotlin-spring/. |

Class table:

| Class name                 | Description                                                                                                                                                                                            |
|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| LocalOpenApiSpecification  | Contains settings with following local repository structure: ``project/domain/version/fileName``. `basePackage` is used as a prefix of generated classes package: `basePackage.project.domain.version` |
| RemoteOpenApiSpecification | Like `LocalOpenApiSpecification`, but contains additional data. `baseUrl` can be set when private gitlab repository is used. `branch` can be set when different branch than `main` is used.            |
| LocalConfiguration         | `relativePath` - relative path to project                                                                                                                                                              |
| GitlabConfiguration        | `projectId` - Gitlab project ID, `accessToken` - Gitlab access token.                                                                                                                                  |
