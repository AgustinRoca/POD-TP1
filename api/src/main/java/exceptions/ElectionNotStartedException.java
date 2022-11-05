package exceptions;

public class ElectionNotStartedException extends ElectionStatusException {
    private String ERROR_MSG = "The election has not been started yet.";

    @Override
    public String getMessage() {
        return this.ERROR_MSG;
    }
}
