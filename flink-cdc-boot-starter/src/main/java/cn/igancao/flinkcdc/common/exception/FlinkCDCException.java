package cn.igancao.flinkcdc.common.exception;

public class FlinkCDCException extends RuntimeException {

    public FlinkCDCException(String errorMsg) {
        super(errorMsg);
    }

    public FlinkCDCException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }

    public FlinkCDCException(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
