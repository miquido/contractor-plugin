import java.net.URI

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Gradle plugin project to get you started.
 * For more details on writing Custom Plugins, please refer to https://docs.gradle.org/8.7/userguide/custom_plugins.html in the Gradle documentation.
 */

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("com.github.gmazzo.buildconfig") version "5.3.5"
    id("maven-publish")
    id("java-gradle-plugin")
    id("signing")
}

group = "com.miquido"
version = "1.0.1"

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

buildConfig {
    buildConfigField("APP_NAME", project.name)
}

dependencies {
    compileOnly(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    implementation("org.openapitools:openapi-generator-gradle-plugin:7.4.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        website = "https://github.com/miquido/contractor-plugin"
        vcsUrl = "https://github.com/miquido/contractor-plugin.git"
        create("contractor") {
            id = "$group.contractor-plugin"
            displayName = "Plugin for OpenAPI specification in repositories"
            description = "Download or copy OpenAPI contract to project and generate REST interfaces. The plugin is preferred for Spring projects"
            implementationClass = "com.miquido.plugin.contractor.ContractorPlugin"
        }
    }
}

publishing {
    afterEvaluate {
        publications {
            withType<MavenPublication> {
                group = project.group
                pom {
                    description.set("Download or copy OpenAPI contract to project and generate REST interfaces. The plugin is preferred for Spring projects")
                    url.set("https://github.com/miquido/contractor-plugin")

                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set("pksiazek-mq")
                            name.set("Przemysław Książek")
                            url.set("https://github.com/pksiazek-mq")
                        }
                    }

                    scm {
                        connection.set("scm:git:git@github.com:miquido/contractor-plugin.git")
                        url.set("https://github.com/miquido/contractor-plugin/tree/master")
                    }
                }
            }

            named<MavenPublication>("pluginMaven") {
                pom.name.set("Plugin for OpenAPI specification in repositories")

            }
            named<MavenPublication>("contractorPluginMarkerMaven") {
                pom.name.set("Plugin for OpenAPI specification in repositories (Gradle plugin marker)")
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("sonatype.user") as String?
                password = findProperty("sonatype.password") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications)
}
