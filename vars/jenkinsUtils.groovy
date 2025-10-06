import com.github.angelcamposm.DescriptionBuilder

/**
 * Updates the current Jenkins job's description with details from pom.xml, composer.json, or package.json.
 */
def updateWorkflowJobDescription() {

    def projectData = getProjectData()

    // Instantiate the helper class with the normalized data
    def builder = new DescriptionBuilder(projectData)
    def descriptionHtml = builder.buildHtmlTable()

    // Use Jenkins credentials for the API token
    withCredentials([usernamePassword(credentialsId: 'jenkins-api-token', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_API_TOKEN')]) {

        // Use shell to execute a curl command to update the job description
        sh label: '[Jenkins] Update WorkflowJob description',
            script: """
            curl \
                --silent \
                --request POST \
                --url ${env.JENKINS_URL}/job/${env.JOB_NAME}/config.xml \
                --user ${JENKINS_USER}:${JENKINS_API_TOKEN} \
                --data-urlencode "description=${descriptionHtml}"
            """
    }

    echo "Successfully updated the job description."
}

private Map getProjectData() {
    def projectData

    // Check for pom.xml, then composer.json, then fall back to package.json
    if (fileExists('pom.xml')) {
        projectData = getProjectDataFromPomXml()
    } else if (fileExists('composer.json')) {
        projectData = getProjectDataFromComposerJson()
    } else if (fileExists('package.json')) {
        projectData = getProjectDataFromPackageJson()
    } else {
        error "Neither pom.xml, composer.json, nor package.json found in the current workspace."
    }

    return projectData
}

private Map getProjectDataFromComposerJson() {
    def composerJson = readJSON file: 'composer.json'

    // Normalize data from composer.json into a standard map
    return [
        name: composerJson.name,
        version: composerJson.version ?: 'N/A', // Version is often not in composer.json
        description: composerJson.description,
        authors: composerJson.authors,
        // License can be a string or an array, so we flatten it into a list
        licenses: [composerJson.license].flatten().findAll { it != null },
        // Use support.source or homepage for the repository URL
        repository: [url: composerJson.support?.source ?: composerJson.homepage]
    ]
}

private Map getProjectDataFromPackageJson() {
    def packageJson = readJSON file: 'package.json'

    // Normalize data from package.json into the same standard map
    // Wrap author and license in lists for consistent processing
    return [
            name: packageJson.name,
            version: packageJson.version,
            description: packageJson.description,
            authors: [packageJson.author].findAll { it != null },
            licenses: [packageJson.license].findAll { it != null },
            repository: packageJson.repository
    ]
}

private Map getProjectDataFromPomXml() {
    def pom = readMavenPom file: 'pom.xml'

    // Normalize data from pom.xml into a standard map
    return [
        name: pom.getName() ?: pom.getArtifactId(),
        version: pom.getVersion(),
        description: pom.description,
        authors: pom.developers,
        licenses: pom.licenses,
        repository: pom.scm
    ]
}