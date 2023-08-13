package io.intoto

import groovy.json.JsonOutput

/*
 | ----------------------------------------------------------------------------
 | Statement
 | ----------------------------------------------------------------------------
 | The Statement is the middle layer of the attestation, binding it to a 
 | particular subject and unambiguously identifying the types of the Predicate.
 |
 | For more information, please visit: 
 | - https://github.com/in-toto/attestation/blob/main/spec/v1/statement.md
 |
 */

def LinkedHashMap statement = [:]

def Void construct() {

    def LinkedHashMap payload = [:]
    
    payload.put('_type', 'https://in-toto.io/Statement/v1')
    payload.put('subject', [])

    statement = payload
}

def Void addProvenance(LinkedHashMap provenance) {
    statement.put('predicateType', 'https://slsa.dev/provenance/v1')
    statement.put('predicate', provenance)
}

def Void addSubject(ResourceDescriptor subject) {
    statement.subject.add(subject.get())
}

def Void print() {
    println(JsonOutput.prettyPrint(toJson()))
}

def String toBase64() {
    return toJson().bytes.encodeBase64().toString()
}

def String toJson() {
    return JsonOutput.toJson(statement)
}

def Void write() {
    writeFile file: 'statement.json', text: toJson()
}
