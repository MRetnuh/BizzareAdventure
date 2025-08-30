package jugadores;

import personajes.Akame;
import personajes.Leone;
import personajes.Personaje;

public class Jugador {
    private Personaje personaje1;
    private Personaje personaje2;

    public Jugador() {
        this.personaje1 = new Akame();
        this.personaje2 = new Leone();
    }

    public Personaje getPersonaje1() {
        return personaje1;
    }

    public Personaje getPersonaje2() {
        return personaje2;
    }

    public void setPosicionesIniciales(float x1, float y1, float x2, float y2) {
        personaje1.cargarUbicaciones(x1, y1);
        personaje2.cargarUbicaciones(x2, y2);
    }
}
