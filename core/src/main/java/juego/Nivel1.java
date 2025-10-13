package juego;

import personajes.Enemigo;

public class Nivel1 extends NivelBase {

    @Override
    protected String getNombreMapa() {
        return "mapacorregido.tmx";
    }

    @Override
    protected void inicializarPropiedadesDelNivel() {

    }

    protected void configurarNivel() {
        this.startX = 200;
        this.startY = 928;

        // Cargar y añadir enemigos
        // NOTA: Si los enemigos no aparecen, verifica que sus texturas se carguen.
        this.enemigos.add(new Enemigo("enemigo1", 600, 928));
        this.enemigos.add(new Enemigo("enemigo2", 800, 928));
    }

    @Override
    public boolean haFinalizado(float nuevaX1, float nuevaY1, float nuevaX2, float nuevaY2) {
        return false;
    }
}