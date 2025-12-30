package critex.core.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CustomExceptionResult {

    private final Integer errorCode;
    private final String message;
    private final Object data;
    private final CustomError code;
    private final LocalDateTime timestamp;

    public CustomExceptionResult(CustomException exception) {
        this.errorCode = exception.getErrorCode();
        this.message = exception.getMessage();
        this.data = exception.getData();
        this.code = exception.getCode();
        this.timestamp = LocalDateTime.now();
    }
}
