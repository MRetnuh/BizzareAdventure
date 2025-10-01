package jugadores;

import java.util.Random;

import personajes.Akame;
import personajes.Leone;
import personajes.Personaje;

//-> ENUM con los personajes -> Asociar a cada elemento del enum las texturas correspondientes a cada personaje
//-> crearPersonaje() -> new Personaje(this.values()[indicePersonaje].parametro1, ...parametro2, ...parametro3)

public class Jugador {
      private boolean partidaEmpezada = false;
	  private Personaje[] personajesJugables = {new Akame(), new Leone()}; //-> FACTORY DE PERSONAJES
	//-> private Personaje personaje = ENUM.crearPersonaje(indiceRandom);
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

public Personaje cambiarPersonaje(float x, float y) {
	int nuevoPersonaje = 0;
	while(nuevoPersonaje == this.numeroPersonajeElegido) {
		nuevoPersonaje= r.nextInt(personajesJugables.length);
	}
	numeroPersonajeElegido = nuevoPersonaje;
	personajeElegido= personajesJugables[numeroPersonajeElegido];
	personajeElegido.cargarUbicaciones(x, y);
	this.personajeElegido.aumentarVida();
	return this.personajeElegido;
}
	  
public Personaje getPersonajeElegido() {
	return this.personajeElegido;
}
public boolean getPartidaEmpezada() {
	return this.partidaEmpezada;
}
}