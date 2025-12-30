package critex.core.utility;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * FILE_ACTION_FAILED:SC_CONFLICT used for file action issues
 * since it indicates an issue with the current state of the resource
 *
 * EXTERNAL_SERVICE_FAILED:SC_EXPECTATION_FAILED to use when an external dependency fails
 * ideal HttpServletResponse would be 424 which was not available
 */
@Getter
public enum CustomError {
    UNSUPPORTED_MEDIA_TYPE(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE),
    ILLEGAL_REQUEST(HttpServletResponse.SC_EXPECTATION_FAILED),
    UNHANDLED_EXCEPTION(HttpServletResponse.SC_PRECONDITION_FAILED),
    SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND),
    HASH_FUNCTION_FAILED(HttpServletResponse.SC_EXPECTATION_FAILED),
    ILLEGAL_ARGUMENT(HttpServletResponse.SC_NOT_ACCEPTABLE),
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED),
    ALREADY_EXIST(HttpServletResponse.SC_CONFLICT),
    FILE_ACTION_FAILED(HttpServletResponse.SC_CONFLICT),
    EXTERNAL_SERVICE_FAILED(HttpServletResponse.SC_EXPECTATION_FAILED),
    INNER_ACTION_FAILED(HttpServletResponse.SC_EXPECTATION_FAILED),;


    private final Integer statusCode;

    CustomError(int statusCode) {
        this.statusCode = statusCode;
    }
}
