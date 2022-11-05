package models;

public enum Party implements Comparable<Party>{
    // NO CAMBIAR EL ORDEN (ORDEN ALFABETICO)

    BUFFALO,
    JACKALOPE,
    LEOPARD,
    LYNX,
    OWL,
    TIGER,
    TURTLE;

    public static Party from(String name){
        return valueOf(name.toUpperCase());
    }

    @Override
    public String toString() {
        return this.name();
    }
}
