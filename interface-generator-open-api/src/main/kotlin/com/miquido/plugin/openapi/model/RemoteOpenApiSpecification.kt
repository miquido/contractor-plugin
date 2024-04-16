package com.miquido.plugin.openapi.model

class RemoteOpenApiSpecification(
    fileName: String,
    basePackage: String,
    project: String,
    domain: String,
    version: String,
    val branch: String = "main"
) : OpenApiSpecification(fileName, basePackage, project, domain, version) {
    override val localization: OpenApiLocalization = OpenApiLocalization.REMOTE
}