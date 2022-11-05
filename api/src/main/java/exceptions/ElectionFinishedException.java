package exceptions;

public class ElectionFinishedException extends ElectionStatusException {
    private String ERROR_MSG = "The election has already finished.";

    @Override
    public String getMessage() {
        return this.ERROR_MSG;
    }
}
