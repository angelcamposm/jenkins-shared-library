package io.intoto.ResourceDescriptors

import groovy.json.JsonOutput

/*
 | ----------------------------------------------------------------------------
 | Resource Descriptor
 | ----------------------------------------------------------------------------
 | A size-efficient description of any software artifact or resource (mutable 
 | or immutable).
 |
 | For more information, please visit: 
 | - https://github.com/in-toto/attestation/blob/main/spec/v1/resource_descriptor.md
 */

def LinkedHashMap rsd = [:]

def String resourceName = ''

def Void file(String fileName) {

    resourceName = fileName

    def LinkedHashMap resourceDescriptor = [:]

    resourceDescriptor.put('name', fileName)
    resourceDescriptor.put('size', getFileSize())
    resourceDescriptor.put('digest', getDigestSet())

    if (sh(script: 'which file || echo "NotFound"', returnStdout: true).trim().toString() == 'NotFound') {
        println('`file` command not found. Skipping mediaType check.')
    } else {
        resourceDescriptor.put('mediaType', getMediaType())
    }
    
    resourceDescriptor.put('createdAt', getTimestamp())

    rsd = resourceDescriptor
}

def LinkedHashMap getDigestSet() {

    def DigestSet ds = new io.intoto.DigestSet()

    ds.construct(resourceName)

    return ds.get()
}

def Void setDigestSet(DigestSet ds) {
    rsd.digest = ds.get()
}

def Integer getFileSize() {
    return sh(
        label: 'Get file size in bytes.',
        script: "stat --format=%s ${resourceName}",
        returnStdout: true
    ).trim().toInteger()
}

def String getMediaType() {
    return sh(
        label: 'Get MIME type.',
        script: "file -b --mime-type ${resourceName}",
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def String getTimestamp() {

    def Long timestamp = sh(
        label: 'Get file Birth date.',
        script: "stat --format=%W ${resourceName}",
        returnStdout: true
    ).trim().toLong()
    
    if (timestamp == 0) {
        timestamp = sh(
            label: 'Get file Access date.',
            script: "stat --format=%X ${resourceName}",
            returnStdout: true
        ).trim().toLong()
    }

    return Timestamp.fromSeconds(timestamp)
}

def LinkedHashMap get() {
    return rsd
}

def Void print() {
    println(JsonOutput.prettyPrint(toJson()))
}

def String toJson() {
    return JsonOutput.toJson(rsd)
}
