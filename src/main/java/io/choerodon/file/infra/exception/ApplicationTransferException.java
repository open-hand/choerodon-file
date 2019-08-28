package io.choerodon.file.infra.exception;

public class ApplicationTransferException extends RuntimeException {

    public ApplicationTransferException(String message) {
        super(message);
    }

    public ApplicationTransferException(String message, Throwable cause) {
        super(message, cause);
    }

}
