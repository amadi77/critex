package critex.core.utility;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private final Integer errorCode;
    private Object data;
    private final CustomError code;

    public CustomException(Integer errorCode ,CustomError code) {
        this.errorCode = errorCode;
        this.code = code;
    }

    public CustomException(Integer errorCode, Object data, String message, CustomError code) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
        this.code = code;
    }

    public CustomException(Integer errorCode, Object data, CustomError code, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.data = data;
        this.code = code;
    }

    public CustomException(Integer errorCode, Object data, CustomError code) {
        this.errorCode = errorCode;
        this.data = data;
        this.code = code;
    }

    public CustomException(Integer errorCode, String message, CustomError code) {
        super(message);
        this.errorCode = errorCode;
        this.code = code;
    }

    public CustomException(Integer errorCode, String message, CustomError code, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.code = code;
    }

}
