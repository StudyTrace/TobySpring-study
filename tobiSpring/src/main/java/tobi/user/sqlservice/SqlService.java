package tobi.user.sqlservice;

public interface SqlService {

    String getSql(String key) throws SqlRetrievalFailureException;

    class SqlRetrievalFailureException extends RuntimeException {

        public SqlRetrievalFailureException(String message) {
            super(message);
        }

        public SqlRetrievalFailureException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
