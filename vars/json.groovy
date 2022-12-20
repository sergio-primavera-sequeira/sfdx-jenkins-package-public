def convertStringIntoJSON(String jsonStr) {
    def json = readJSON text: jsonStr //need the Pipeline Utility Steps plugin
    return json
}
