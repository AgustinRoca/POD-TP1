package exceptions;

public class ElectionStartedException extends ElectionStatusException {
    private String ERROR_MSG = "The election has already started.";

    @Override
    public String getMessage() {
        return this.ERROR_MSG;
    }
}
