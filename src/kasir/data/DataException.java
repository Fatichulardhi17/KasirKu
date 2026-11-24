package kasir.data;

/** Kesalahan saat membaca atau menyimpan data lokal. */
public class DataException extends RuntimeException {

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
