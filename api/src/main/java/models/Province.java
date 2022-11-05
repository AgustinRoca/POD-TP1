package models;

public enum Province {
    JUNGLE,
    SAVANNAH,
    TUNDRA;

    public static Province from(String name){
         return valueOf(name.toUpperCase());
    }
}
