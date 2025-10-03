package jugadores;

import java.util.Random;

import personajes.Akame;
import personajes.Leone;
import personajes.Personaje;

enum PersonajeTipo {
    AKAME("Akame", "assets/akame_texture.png"),
    LEONE("Leone", "assets/leone_texture.png");

    private final String nombreClase;
    private final String rutaTextura;

    PersonajeTipo(String nombreClase, String rutaTextura) {
        this.nombreClase = nombreClase;
        this.rutaTextura = rutaTextura;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public String getRutaTextura() {
        return rutaTextura;
    }
}

class PersonajeF {//-->(este es el factory no sabia que nombre ponerle xd)
    public static Personaje crearPersonaje(PersonajeTipo tipo) {
        switch (tipo) {
            case AKAME:
                return new Akame();
            case LEONE:
                return new Leone();
            default:
                return null;
        }
    }
}
public class Jugador {
    private boolean partidaEmpezada = false;
    private Personaje personajeElegido;
    private Random r = new Random();

    public void generarPersonajeAleatorio() {
        PersonajeTipo[] tipos = PersonajeTipo.values();
        PersonajeTipo tipoElegido = tipos[r.nextInt(tipos.length)];
        this.personajeElegido = PersonajeF.crearPersonaje(tipoElegido);
        this.partidaEmpezada = true;
    }

    // No se necesita el método getListaPersonajes() si se usa la fábrica para crear personajes
    // Si aún se quisiera, se podría devolver un array de los tipos de enum disponibles

    public PersonajeTipo[] getTiposPersonajes() {
        return PersonajeTipo.values();
    }

    public Personaje cambiarPersonaje(float x, float y) {
        PersonajeTipo[] tipos = PersonajeTipo.values();
        PersonajeTipo tipoActual = null;

        // Encuentra el tipo del personaje actual para evitar repetirlo
        for (PersonajeTipo tipo : tipos) {
            if (personajeElegido.getClass().getSimpleName().equals(tipo.getNombreClase())) {
                tipoActual = tipo;
                break;
            }
        }

        PersonajeTipo nuevoTipo;
        do {
            nuevoTipo = tipos[r.nextInt(tipos.length)];
        } while (nuevoTipo == tipoActual);

        this.personajeElegido = PersonajeF.crearPersonaje(nuevoTipo);
        this.personajeElegido.cargarUbicaciones(x, y);
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