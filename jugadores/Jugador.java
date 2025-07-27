package jugadores;

import java.util.Random;

import personajes.Akame;
import personajes.Leone;
import personajes.Personaje;

public class Jugador {
	  private Personaje[] personajesJugables = {new Akame(), new Leone()};
	  private int numeroPersonajeElegido;
	  private Personaje personajeElegido;
	  
public void generarPersonajeAleatorio() {
	Random r = new Random();
	numeroPersonajeElegido= r.nextInt(personajesJugables.length);
	personajeElegido = personajesJugables[numeroPersonajeElegido];
}

public Personaje[] getListaPersonajes() {
	return this.personajesJugables;
}
	  
public Personaje getPersonajeElegido() {
	return this.personajeElegido;
}
}
