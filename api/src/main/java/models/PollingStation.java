package models;

import java.io.Serializable;
import java.util.Objects;

public class PollingStation implements Serializable {
    private final int number;

    public PollingStation(int number) {
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollingStation that = (PollingStation) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return String.valueOf(this.number);
    }
}
