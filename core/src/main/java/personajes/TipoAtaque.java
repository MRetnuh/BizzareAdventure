package personajes;

public enum TipoAtaque {
    MELEE("Melee"), DISTANCIA("Distancia");

    final String tipo;

    TipoAtaque(String tipo){
        this.tipo = tipo;
    }

    public String getTipo() {
        return this.tipo;
    }
}
