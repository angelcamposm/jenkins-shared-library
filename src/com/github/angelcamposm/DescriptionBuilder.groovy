package com.github.angelcamposm

import org.opensource.LicenseLink

class DescriptionBuilder {

    private static final long serialVersionUID = 1L

    private def projectData

    DescriptionBuilder(def projectData) {
        this.projectData = projectData
    }

    /**
     * Builds an HTML table with project details.
     * @return A string containing the HTML table.
     */
    String buildHtmlTable() {

        def table = new StringBuilder()
        table.append('<table class="jenkins-table jenkins-table--medium">')
        table.append('<thead>')
        table.append('<tr>')
        table.append('<td colspan="2" style="color: dimgray; letter-spacing: 1px; padding: 20px 5px 0 5px;">')
        table.append('<b>Project Details</b>')
        table.append('</td>')
        table.append('</tr>')
        table.append('</thead>')

        // Table Rows using normalized projectData
        addRow(table, "Project Name", projectData.name)
        addRow(table, "Version", projectData.version)
        addRow(table, "Description", projectData.description)
        addRow(table, "Engine / Platform", formatEngine(projectData.engine))
        addRow(table, "Framework", formatFramework(projectData.framework))
        addRow(table, "Author(s)", formatAuthors(projectData.authors))
        addRow(table, "License(s)", formatLicenses(projectData.licenses))
        addRow(table, "Repository", formatRepository(projectData.repository))

        table.append("</table>")

        return table.toString()
    }

    private static void addRow(StringBuilder table, String key, Object value) {
        table.append("<tr>")
        table.append("<td>${key}</td>")
        table.append("<td>${value ?: 'N/A'}</td>")
        table.append("</tr>")
    }

    /**
     * Formats a list of authors for display. Handles data from pom.xml, composer.json, and package.json.
     */
    private static String formatAuthors(List authors) {
        if (authors == null || authors.isEmpty()) return 'N/A'

        return authors.collect { author ->
            if (author instanceof String) {
                return author // Handles simple string from package.json
            }
            if (author instanceof Map) {
                def name = author.name ?: author.id ?: 'N/A' // pom.xml uses name or id
                def email = author.email ?: ""

                if (email) {
                    return '<a class="jenkins-table__link" href="mailto:' + email + '" target="_blank">' + "${name} &lt;${email}&gt;" + '</a>'
                }

                return "${name} &lt;${email}&gt;"
            }
            return 'N/A'
        }.join('<br>')
    }

    /**
     * Formats the engine information for display.
     */
    private static String formatEngine(Map engine) {
        if (engine == null || !engine.version) return 'N/A'
        return "${engine.type}: ${engine.version}"
    }

    private static String formatFramework(Map framework) {
        if (framework == null || !framework.version) return 'N/A'
        def version = framework.version.replaceAll('~|\\^', '')
        return "${framework.name}: ${version}"
    }

    /**
     * Formats a list of licenses into clickable links if a URL is provided.
     */
    private static String formatLicenses(List licenses) {

        if (licenses == null || licenses.isEmpty()) return 'N/A'

        return licenses.collect { license ->
            if (license instanceof String) return LicenseLink.fromLicense(license)
            if (license instanceof Map) return LicenseLink.fromLicense(license)
            return 'N/A'
        }.join('<br>')
    }

    /**
     * Formats the repository object/string into a clickable link.
     */
    private static String formatRepository(Object repo) {
        if (repo == null) return 'N/A'

        def url = ''

        if (repo instanceof String) { // Handles simple string from package.json
            url = repo
        } else if (repo instanceof Map && repo.url) {
            // Handles package.json { "url": "..." }, pom.xml scm { "url": "..." }, and composer.json support/homepage
            url = repo.url
        } else {
            return 'N/A'
        }

        def displayUrl = url.replace("git+", "").replace(".git", "")

        if (displayUrl.startsWith('http')) {
            return '<a class="jenkins-table__link" href="' + displayUrl + '" target="_blank">' + displayUrl + '</a>'
        }
        return displayUrl
    }
}
