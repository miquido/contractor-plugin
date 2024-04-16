package com.miquido.plugin.openapi.model

class LocalOpenApiSpecification(
    fileName: String,
    basePackage: String,
    project: String,
    domain: String,
    version: String,
) : OpenApiSpecification(fileName, basePackage, project, domain, version) {
    override val localization: OpenApiLocalization = OpenApiLocalization.LOCAL
}