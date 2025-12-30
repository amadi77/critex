package critex.core.utility;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CustomExceptionContent {
    private Integer errorCode;
    private String messageKey;
    private Map<String, String> messages;
}
