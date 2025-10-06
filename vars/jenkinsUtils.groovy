import com.github.angelcamposm.DescriptionBuilder

/**
 * Updates the current Jenkins job's description with details from pom.xml, composer.json, or package.json.
 *
 * @requires Pipeline Utility Steps (pipeline-utility-steps)
 */
def updateWorkflowJobDescription() {
    def projectData = getProjectData()

    // Instantiate the helper class with the normalized data
    def builder = new DescriptionBuilder(projectData)
    String descriptionHtml = builder.buildHtmlTable()

    currentBuild.rawBuild.project.setDescription(descriptionHtml)
}

private Map getProjectData() {
    // The order of cases determines the precedence: composer.json > package.json > pom.xml
    switch (true) {
        case fileExists('composer.json'):
            return getProjectDataFromComposerJson()
        case fileExists('package.json'):
            return getProjectDataFromPackageJson()
        case fileExists('pom.xml'):
            return getProjectDataFromPomXml()
        default:
            error('Neither pom.xml, composer.json, nor package.json found in the current workspace.')
    }
}

private Map getProjectDataFromComposerJson() {
    def composerJson = readJSON file: 'composer.json'

    // Normalize data from composer.json into a standard map
    return [
        name       : composerJson.name ?: 'N/A',
        version    : composerJson.version ?: 'N/A',
        description: composerJson.description ?: '',
        // Ensure authors is always a list
        authors    : [composerJson.authors].flatten().findAll { it != null },
        // License can be a string or an array, flatten to handle both
        licenses   : [composerJson.license].flatten().findAll { it != null },
        // Use support.source or homepage for the repository URL
        repository : [url: composerJson.support?.source ?: composerJson.homepage],
        // Extract PHP version
        engine     : [type: 'PHP', version: composerJson.require?.php]
    ]
}

private Map getProjectDataFromPackageJson() {
    def packageJson = readJSON file: 'package.json'

    // Normalize data from package.json into the same standard map
    return [
        name       : packageJson.name ?: 'N/A',
        version    : packageJson.version ?: 'N/A',
        description: packageJson.description ?: '',
        // Ensure author is always a list, even if it's a single string/object
        authors    : [packageJson.author].flatten().findAll { it != null },
        // Ensure license is always a list
        licenses   : [packageJson.license].flatten().findAll { it != null },
        repository : packageJson.repository,
        // Extract Node.js version
        engine     : [type: 'Node.js', version: packageJson.engines?.node]
    ]
}

private Map getProjectDataFromPomXml() {
    def pom = readMavenPom file: 'pom.xml'

    // Normalize data from pom.xml into a standard map
    return [
        name       : pom.getName() ?: pom.getArtifactId() ?: 'N/A',
        version    : pom.getVersion() ?: 'N/A',
        description: pom.description ?: '',
        // Ensure authors and licenses are always lists
        authors    : pom.developers ?: [],
        licenses   : pom.licenses ?: [],
        repository : pom.scm,
        // Extract Java version
        engine     : [type: 'Java', version: getJavaVersionFromPom(pom)]
    ]
}

/**
 * Helper to extract the Java version from a Maven POM object.
 * It checks common properties in a specific order.
 */
private String getJavaVersionFromPom(def pom) {
    if (! pom.properties) return null

    // Check for common properties used to define the Java version
    return pom.properties.getProperty('maven.compiler.release') ?:
           pom.properties.getProperty('maven.compiler.source') ?:
           pom.properties.getProperty('java.version')
}