package jugadores;

import java.util.Random;
import personajes.FabricaDePersonajes;
import personajes.Personaje;

public class Jugador {
    private boolean partidaEmpezada = false;
    private Personaje personajeElegido;
    private final Random r = new Random();
    private int indicePersonaje;
    private final FabricaDePersonajes[] personajesDisponibles = FabricaDePersonajes.values();
    private FabricaDePersonajes elegido;

    public void generarPersonajeAleatorio() {
        indicePersonaje = r.nextInt(personajesDisponibles.length);
        elegido = personajesDisponibles[indicePersonaje];
        personajeElegido = elegido.crear();
        partidaEmpezada = true;
    }

    public Personaje cambiarPersonaje(float x, float y) {
        int nuevoIndice;
        do {
            nuevoIndice = r.nextInt(personajesDisponibles.length);
        } while (nuevoIndice == indicePersonaje);

        indicePersonaje = nuevoIndice;
        elegido = personajesDisponibles[indicePersonaje];
        personajeElegido = elegido.crear();
        personajeElegido.cargarUbicaciones(x, y);
        personajeElegido.aumentarVida();
        return personajeElegido;
    }

    public Personaje getPersonajeElegido() {
        return personajeElegido;
    }

    public boolean getPartidaEmpezada() {
        return partidaEmpezada;
    }
}
