package com.miquido.plugin.contractor.model

class LocalOpenApiSpecification(
    fileName: String,
    basePackage: String,
    project: String,
    domain: String,
    version: String,
) : OpenApiSpecification(fileName, basePackage, project, domain, version) {
    override val localization: OpenApiLocalization = OpenApiLocalization.LOCAL
}
