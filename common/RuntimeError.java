package common;

import java.lang.Error;
import java.lang.Throwable;

public class RuntimeError extends Error {
    public RuntimeError() {
        super();
    }

    public RuntimeError(String message) {
        super(message);
    }

    public RuntimeError(String message, Throwable cause) {
        super(message, cause);
    }
}
