package io.intoto.ResourceDescriptors

import groovy.json.JsonOutput
import io.intoto.ResourceDescriptor

def String gitBranch = 'master'
def ResourceDescriptor resourceDescriptor = new io.intoto.ResourceDescriptor()

def Void construct() {

    resourceDescriptor
        .name(getBranchNameFromParams())
        .digest([gitCommit: getGitCommit()])
        .uri(getGitRemoteUrl())
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
    resourceDescriptor.addAnnotation(key, value)
}

def Void setName(String name) {
    resourceDescriptor.name(name)
}

def ResourceDescriptor get() {
    return resourceDescriptor
}

def Void print() {
    resourceDescriptor.print()
}

def String toJson() {
    return resourceDescriptor.toJson()
}