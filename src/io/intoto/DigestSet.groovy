package io.intoto

import groovy.json.JsonOutput

/*
 | ----------------------------------------------------------------------------
 | DigestSet
 | ----------------------------------------------------------------------------
 | A set of cryptographic digests of the contents of the resource or artifact.
 |
 | Supported algorithms:
 | sha256, sha224, sha384, sha512, sha512_224, sha512_256, sha3_224, sha3_256, 
 | sha3_384, sha3_512, shake128, shake256, blake2b, blake2s, ripemd160, sm3, 
 | gost, sha1, md5
 |
 | For more information, please visit: 
 | - https://github.com/in-toto/attestation/blob/main/spec/v1/digest_set.md
 */

def LinkedHashMap digestSet = [:]
def String resourceName = ''

def Void construct(String fileName) {

    resourceName = fileName

    def LinkedHashMap payload = [:]

    payload.put('md5', getMd5Sum())
    payload.put('sha1', getSha1Sum())
    payload.put('sha256', getSha256Sum())
    payload.put('sha512', getSha512Sum())

    digestSet = payload
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

def LinkedHashMap get() {
    return digestSet
}

def Void print() {
    println(JsonOutput.prettyPrint(toJson()))
}

def String toJson() {
    return JsonOutput.toJson(digestSet)
}