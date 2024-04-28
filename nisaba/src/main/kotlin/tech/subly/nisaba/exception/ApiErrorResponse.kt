package tech.subly.nisaba.exception

data class ApiErrorResponse(
    var timestamp: Long,
    var status: Int,
    var error: String,
    var path: String,
    var description: String
)
