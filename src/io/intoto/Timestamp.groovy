package io.intoto

class Timestamp {

    static String fromMilliseconds(Long millis) {
        def String startTime = new Date(millis).format("yyyy-MM-dd'T'h:m:ss.SSS")
        return "${startTime}Z"
    }

    static String get() {
        def String startTime = new Date().format("yyyy-MM-dd'T'h:m:ss.SSS")
        return "${startTime}Z"
    }
}