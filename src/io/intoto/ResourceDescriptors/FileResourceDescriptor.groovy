package io.intoto.ResourceDescriptors

import groovy.json.JsonOutput
import io.intoto.DigestSet
import io.intoto.ResourceDescriptor
import io.intoto.Timestamp

def String resourceName = ''
def ResourceDescriptor resourceDescriptor = null

def Void file(String fileName) {

    resourceName = fileName

    resourceDescriptor = new ResourceDescriptor()

    resourceDescriptor
        .name(fileName)
        .digest(getDigestSet())
        .addAnnotation('size', getFileSize())
        .addAnnotation('createdAt', getCreatedAtTimestamp())

    if (sh(script: 'which file || echo "NotFound"', returnStdout: true).trim().toString() == 'NotFound') {
        println('`file` command not found. Skipping mediaType check.')
    } else {
        resourceDescriptor.mediaType(getMediaType())
    }
}

def LinkedHashMap getDigestSet() {

    def DigestSet ds = new io.intoto.DigestSet()

    ds.construct(resourceName)

    return ds.get()
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

def String getCreatedAtTimestamp() {

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

def ResourceDescriptor get() {
    return resourceDescriptor
}

def Void print() {
    resourceDescriptor.print()
}

def String toJson() {
    return resourceDescriptor.toJson()
}


def Void write(Boolean pretty = true) {
        
    def String payload = pretty 
        ? JsonOutput.prettyPrint(toJson())
        : toJson()

    def String fileName = "${resourceName.replace('.', '-')}.resourceDescriptor.json"
    
    writeFile encoding: 'UTF-8', file: fileName, text: payload
}