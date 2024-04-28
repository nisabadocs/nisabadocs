package tech.subly.nisaba.exception

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import jakarta.ws.rs.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.security.access.AccessDeniedException

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMethodArgumentNotValid(e: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<Any> {
        logger.error(e.message, e)
        if (e.cause is MissingKotlinParameterException) {
            val missingParamEx = e.cause as MissingKotlinParameterException
            val error = ApiErrorResponse(
                timestamp = System.currentTimeMillis(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Bad Request",
                path = request.getDescription(false),
                description = "Missing or invalid parameter: ${missingParamEx.parameter.name}"
            )
            return ResponseEntity(error, HttpStatus.BAD_REQUEST)
        }
        val response = ApiErrorResponse(
            timestamp = System.currentTimeMillis(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            path = request.getDescription(false),
            description = "Missing or invalid parameter"
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Validation error: " + e.bindingResult.fieldError?.defaultMessage, e)
        val response = ApiErrorResponse(
            timestamp = System.currentTimeMillis(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            path = request.getDescription(false),
            description = "Validation error: " + e.bindingResult.fieldError?.defaultMessage
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    // Example handler for a custom exception, such as a not found exception
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResourceNotFound(e: ResponseStatusException, request: WebRequest): ResponseEntity<Any> {
        logger.error("ResponseStatusException", e)
        val response = ApiErrorResponse(
            timestamp = System.currentTimeMillis(),
            status = e.statusCode.value(),
            error = e.message,
            path = request.getDescription(false),
            description = e.reason ?: "Server Error"
        )
        return ResponseEntity(response, e.statusCode)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleResourceNotFound(e: NotFoundException, request: WebRequest): ResponseEntity<Any> {
        logger.error("ResponseStatusException", e)
        val response = ApiErrorResponse(
            timestamp = System.currentTimeMillis(),
            status = HttpStatus.NOT_FOUND.value(),
            error = e.message ?: "Not Found",
            path = request.getDescription(false),
            description = "Not Found"
        )
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Access Denied Exception", e)
        val response = ApiErrorResponse(
            timestamp = System.currentTimeMillis(),
            status = HttpStatus.FORBIDDEN.value(),
            error = "Access Denied",
            path = request.getDescription(false),
            description = "You do not have permission to access this resource"
        )

        return ResponseEntity(response, HttpStatus.FORBIDDEN)
    }

    // Generic exception handler as a fallback
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(e: Exception, request: WebRequest): ResponseEntity<Any> {
        logger.error("Internal Server error", e)
        val response = ApiErrorResponse(
            timestamp = System.currentTimeMillis(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Server Error",
            path = request.getDescription(false),
            description = "An unexpected error occurred. Please try again later."
        )

        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
