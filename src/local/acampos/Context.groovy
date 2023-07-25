package local.acampos

import groovy.json.JsonOutput

def LinkedHashMap context = [:]

def Void initialize() {
    context = [
        kind: 'WorkflowRunEnvelope',
        version: 'v1',
        data: []
    ]

    context.data.add(getWorkflowRunInfo())

    println(JsonOutput.toJson(context))
}

def LinkedHashMap getWorkflowRunInfo() {
    return [
        kind: 'workflowRun',
        metadata: [
            agent: 'docker',
            displayName: currentBuild.displayName,
            duration: currentBuild.duration,
            failedAt: '',
            id: currentBuild.id,
            launchedAt: currentBuild.startTimeInMillis,
            launchedBy: currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')[0].userName,
            name: currentBuild.projectName,
            number: currentBuild.number,
            parameters: params.toString(),
            result: currentBuild.currentResult,
            server: 'jenkins-localhost',
            url: currentBuild.absoluteUrl,
            waitTime: currentBuild.timeInMillis - currentBuild.startTimeInMillis
        ]
    ]
}