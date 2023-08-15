package io.intoto.ResourceDescriptors

def String gitBranch = 'master'
def LinkedHashMap resourceDescriptor = [:]

def Void construct() {

    def LinkedHashMap payload = [:]

    payload.put('name', 'git')
    payload.put('digest', getGitCommit())
    payload.put('uri', getGitRemoteUrl())

    resourceDescriptor = payload
}

def LinkedHashMap getGitCommit() {

    def String hash = sh(
        label: 'Get git commit.',
        script: 'git rev-parse --verify HEAD',
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()

    return [
        gitCommit: hash
    ]
}

def String getGitRemoteUrl() {

    def String remote = sh(
        label: 'Get git remote origin.',
        script: 'git remote get-url origin',
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()

    return "git+${remote}"
}

def Void addAnnotation(String key, value) {

    if (!resourceDescriptor.containsKey('annotations')) {
        resourceDescriptor.put('annotations', [:])
    }

    resourceDescriptor.annotations.put(key, value)
}

def Void setBranch(String branch) {
    gitBranch = branch.contains('*/') ? branch.replace('*/') : branch
}

def Void setName(String name) {
    resourceDescriptor.name = name
}