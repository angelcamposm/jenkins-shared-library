package io.intoto

class Timestamp {
    
    static String fromMilliseconds(Integer millis) {
        def String startTime = new Date(currentBuild.startTimeInMillis).format("yyyy-MM-dd'T'h:m:ss.SSS")
        return "${startTime}Z"
    }

    static String get() {
        def String startTime = new Date().format("yyyy-MM-dd'T'h:m:ss.SSS")
        return "${startTime}Z"
    }
}