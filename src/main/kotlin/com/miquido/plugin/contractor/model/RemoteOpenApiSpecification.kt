package com.miquido.plugin.contractor.model

class RemoteOpenApiSpecification(
    fileName: String,
    basePackage: String,
    project: String,
    domain: String,
    version: String,
    val baseUrl: String = "https://gitlab.com",
    val branch: String = "main"
) : OpenApiSpecification(fileName, basePackage, project, domain, version) {
    override val localization: OpenApiLocalization = OpenApiLocalization.REMOTE
}
