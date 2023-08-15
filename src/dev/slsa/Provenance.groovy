package dev.slsa

import groovy.json.JsonOutput
import io.intoto.Timestamp

def LinkedHashMap provenance = [:]

def Void construct() {
    
    def LinkedHashMap payload = [:]

    payload.put('buildDefinition', [:])
    payload.put('runDetails', [:])
    
    payload.buildDefinition.put('buildType', 'https://www.jenkins.io/Pipeline')
    payload.buildDefinition.put('externalParameters', [:])
    payload.buildDefinition.put('internalParameters', [:])
    payload.buildDefinition.put('resolvedDependencies', [])
    
    payload.buildDefinition.internalParameters = getInternalParameters()

    payload.runDetails.put('builder', [:])
    payload.runDetails.put('metadata', [:])
    payload.runDetails.put('byproducts', [])
    
    payload.runDetails.builder.put('id', getBuilderId())
    payload.runDetails.builder.put('builderDependencies', [])
    payload.runDetails.builder.put('version', '')
    
    payload.runDetails.metadata.put('invocationId', getInvocationId())
    payload.runDetails.metadata.put('startedOn', getStartTimestamp())
    payload.runDetails.metadata.put('finishedOn', '')    

    provenance = payload
}

def String getBuilderId() {
    return "${env.BUILD_URL}"
}

def String getInvocationId() {
    return "#${currentBuild.getNumber()}"
}

def String getStartTimestamp() {
    return Timestamp.fromMilliseconds(currentBuild.startTimeInMillis)
}

def LinkedHashMap getInternalParameters() {
    
    def LinkedHashMap parameters = [:]

    for (item in params) {
        parameters.put(item.getKey(), item.getValue())
    }

    return parameters
}

def Void addBuilderDependency(LinkedHashMap dependency) {
    provenance.buildDefinition.resolvedDependencies.add(dependency)
}

def Void finish() {
    provenance.runDetails.metadata.finishedOn = Timestamp.get()
}

def LinkedHashMap get() {
    return provenance
}

def Void print() {
    println(JsonOutput.prettyPrint(toJson()))
}

def String toJson() {
    return JsonOutput.toJson(provenance)
}

def Void write(Boolean pretty = true) {
    
    def String payload = pretty 
        ? JsonOutput.prettyPrint(toJson())
        : toJson()

    writeFile encoding: 'UTF-8', file: 'provenance.json', text: payload
}
