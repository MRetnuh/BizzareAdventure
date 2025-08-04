package jugadores;

import java.util.Random;

import personajes.Akame;
import personajes.Leone;
import personajes.Personaje;

public class Jugador {
      private boolean partidaEmpezada = false;
	  private Personaje[] personajesJugables = {new Akame(), new Leone()};
	  private int numeroPersonajeElegido;
	  private Personaje personajeElegido;
	  private Random r = new Random();
public void generarPersonajeAleatorio() {
	numeroPersonajeElegido= r.nextInt(personajesJugables.length);
	personajeElegido = personajesJugables[numeroPersonajeElegido];
	this.partidaEmpezada = true;
}

public Personaje[] getListaPersonajes() {
	return this.personajesJugables;
}

public void cambiarPersonaje() {
	int nuevoPersonaje = 0;
	while(nuevoPersonaje == this.numeroPersonajeElegido) {
		nuevoPersonaje= r.nextInt(personajesJugables.length);
	}
	numeroPersonajeElegido = nuevoPersonaje;
	personajeElegido= personajesJugables[numeroPersonajeElegido];
	this.personajeElegido.aumentarVida();
}
	  
public Personaje getPersonajeElegido() {
	return this.personajeElegido;
}
public boolean getPartidaEmpezada() {
	return this.partidaEmpezada;
}
}
