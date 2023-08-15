package io.intoto.ResourceDescriptors

import groovy.json.JsonOutput

def String gitBranch = 'master'
def LinkedHashMap resourceDescriptor = [:]

def Void construct() {

    def LinkedHashMap payload = [:]

    payload.put('name', getBranchNameFromParams())
    payload.put('digest', getGitCommit())
    payload.put('uri', getGitRemoteUrl())

    resourceDescriptor = payload
}

def String cleanBranchName(String branch) {
    return branch.contains('*/') 
        ? branch.replace('*/', '') 
        : branch
}

def String getBranchNameFromParams() {

    if (params.BRANCH) {
        return cleanBranchName(params.BRANCH)
    }

    return 'git'
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

    println(remote)

    if (remote.startsWith('git@')) {
        return remote
    }

    return "git+${remote}"
}

def Void addAnnotation(String key, value) {

    if (!resourceDescriptor.containsKey('annotations')) {
        resourceDescriptor.put('annotations', [:])
    }

    resourceDescriptor.annotations.put(key, value)
}

def Void setBranch(String branch) {
    gitBranch = cleanBranchName(branch)
}

def Void setName(String name) {
    resourceDescriptor.name = name
}

def LinkedHashMap get() {
    return resourceDescriptor
}

def Void print() {
    println(JsonOutput.prettyPrint(toJson()))
}

def String toJson() {
    return JsonOutput.toJson(resourceDescriptor)
}