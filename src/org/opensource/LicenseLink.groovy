package org.opensource

class LicenseLink {

    final static String URL = 'https://opensource.org/licenses/'

    private static buildLicenseUrl(String license) {
        return URL + license.toLowerCase()
    }

    static String fromLicense(String license) {
        '<a class="jenkins-table__link" href="' + buildLicenseUrl(license) + ' " target="_blank">' + license + '</a>'
    }
}
