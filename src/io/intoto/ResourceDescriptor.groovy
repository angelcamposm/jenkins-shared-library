package io.intoto

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

/**
 * A set of cryptographic digests of the contents of the resource or artifact.
 *
 * @return LinkedHashMap
 */
def LinkedHashMap getDigestSet() {
    def LinkedHashMap digestSet = [:]

    digestSet.put('md5', getMd5Sum())
    digestSet.put('sha1', getSha1Sum())
    digestSet.put('sha256', getSha256Sum())
    digestSet.put('sha512', getSha512Sum())

    return digestSet
}

def String getMd5Sum() {
    return sh(
        label: 'Get MD5 (128-bit) checksums.',
        script: "md5sum ${resourceName} | awk '{print \$1}'",
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def String getSha1Sum() {
    return sh(
        label: 'Get SHA1 (160-bit) checksums.', 
        script: "sha1sum ${resourceName} | awk '{print \$1}'", 
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def String getSha256Sum() {
    return sh(
        label: 'Get SHA256 (256-bit) checksums.', 
        script: "sha256sum ${resourceName} | awk '{print \$1}'", 
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
}

def String getSha512Sum() {
    return sh(
        label: 'Get SHA512 (512-bit) checksums.', 
        script: "sha512sum ${resourceName} | awk '{print \$1}'", 
        returnStdout: true,
        encoding: 'UTF-8'
    ).trim().toString()
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

    def Integer timestamp = sh(
        label: 'Get file Birth date.',
        script: "stat --format=%W ${resourceName}",
        returnStdout: true
    ).trim().toInteger()
    
    if (timestamp == 0) {
        timestamp = sh(
            label: 'Get file Access date.',
            script: "stat --format=%X ${resourceName}",
            returnStdout: true
        ).trim().toInteger()
    }

    return sh(
        label: 'Convert epoch timestamp to RFC3339.',
        script: "date --date=@${timestamp} +%Y-%m-%dT%H:%M:%S.%sZ",
        returnStdout: true
    ).trim().toString()
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
