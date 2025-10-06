package org.opensource

class LicenseLink {

    final static String URL = 'https://opensource.org/licenses/'

    private static buildLicenseUrl(String license) {
        return URL + license.toLowerCase()
    }

    static String fromLicense(String license) {
        return '<a class="jenkins-table__link" href="' + buildLicenseUrl(license) + ' " target="_blank">' + license + '</a>'
    }

    static String fromLicenseWithUrl(String license, String url) {
        return '<a class="jenkins-table__link" href="' + url + ' " target="_blank">' + license + '</a>'
    }

    static String fromLicense(Map license) {

        def name = license.name ?: 'N/A' // pom.xml uses name
        def url = license.url

        if (url) {
            return fromLicenseWithUrl(name.toString(), url.toString())
        }

        return fromLicense(name.toString())
    }
}
