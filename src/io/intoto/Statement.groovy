package io.intoto

import groovy.json.JsonOutput

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

def Void addSubject(LinkedHashMap subject) {
    statement.subject.add(subject)
}

def Void print() {
    println(JsonOutput.prettyPrint(this.toJson())
}

def String toJson() {
    return JsonOutput.toJson(this.payload)
}

def Void write() {
    writeFile file: '', text: toJson()
}
