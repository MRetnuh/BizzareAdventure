package mecanicas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Stage;
import audios.Musica;
import enemigos.EnemigoBase;
import juego.Partida;
import jugadores.Jugador;
import niveles.Nivel1;
import niveles.Nivel2;
import niveles.NivelBase;
import pantallas.NivelSuperado;
import personajes.Personaje;

public class GestorNiveles {

    private NivelBase[] niveles = {new Nivel1(), new Nivel2()};
    private int indiceNivelActual = 0;
    private NivelBase nivelActual = this.niveles[this.indiceNivelActual];
    private GestorDerrota gestorDerrota;
    private final Game juego;

    public GestorNiveles(Game juego, GestorDerrota gestorDerrota) {
        this.juego = juego;
        this.gestorDerrota = gestorDerrota;
    }

    public void inicializarNivel(Stage stage, Jugador[] jugadores) {
        if (this.nivelActual == null) return;

        this.nivelActual.restaurarEstadoCajas();
        this.nivelActual.crearEnemigos();

        stage.clear();

        // Inicializar jugadores: primero generar si no tienen personaje
        for (Jugador j : jugadores) {
            if (j.getPersonajeElegido() == null) {
                j.generarPersonajeAleatorio();
            }
        }

        // Setear posiciones y agregar al stage
        if (jugadores[0].getPersonajeElegido() != null) {
            Personaje p1 = jugadores[0].getPersonajeElegido();
            p1.setPosicion(this.nivelActual.getInicioX1(), this.nivelActual.getInicioY1());
            stage.addActor(p1);
        }

        if (jugadores[1].getPersonajeElegido() != null) {
            Personaje p2 = jugadores[1].getPersonajeElegido();
            p2.setPosicion(this.nivelActual.getInicioX2(), this.nivelActual.getInicioY2());
            stage.addActor(p2);
        }

        // Agregar enemigos al stage
        for (EnemigoBase enemigo : this.nivelActual.getEnemigos()) {
            stage.addActor(enemigo);
        }

        // Resetear gestor de derrota
        this.gestorDerrota.resetear();
    }

    public void comprobarVictoria(Jugador[] jugadores, Partida partida) {
        Personaje p1 = jugadores[0].getPersonajeElegido();
        Personaje p2 = jugadores[1].getPersonajeElegido();
        if (p1 == null || p2 == null) return;

        if (this.nivelActual.comprobarVictoria(p1.getX(), p1.getY(), p2.getX(), p2.getY())) {
            this.indiceNivelActual++;
            if (this.indiceNivelActual < this.niveles.length) {
                NivelSuperado nivelSuperado = new NivelSuperado(
                        this.nivelActual.getNombreNivel(),
                        this.juego,
                        this.niveles[this.indiceNivelActual].getNombreNivel(),
                        partida
                );
                this.juego.setScreen(nivelSuperado);
            }
        }
    }

    public void inicializarSiguienteNivel(Stage stage, Jugador[] jugadores) {
        if (this.indiceNivelActual >= this.niveles.length) return;

        this.nivelActual = this.niveles[this.indiceNivelActual];
        inicializarNivel(stage, jugadores);

        // Reiniciar movimiento para que los personajes puedan moverse
        for (Jugador j : jugadores) {
            if (j.getPersonajeElegido() != null) j.getPersonajeElegido().detenerMovimiento();
        }
    }
}
