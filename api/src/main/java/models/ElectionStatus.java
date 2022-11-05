package models;

import java.io.Serializable;

public enum ElectionStatus implements Serializable {
    UNINITIALIZED("The elections have not started"),
    OPEN("The elections are open"),
    FINISHED("The elections are closed");

    private final String string;
    private static final long serialVersionUID = 1L;

    ElectionStatus(String s) {
        string = s;
    }

    @Override
    public String toString() {
        return string;
    }
}
