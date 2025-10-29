// mecanicas/GestorCombate.java
package mecanicas;

import java.util.Iterator;
import enemigos.EnemigoBase;
import personajes.Personaje;
import jugadores.Jugador;
import proyectiles.Proyectil;
import personajes.TipoAtaque;
import enemigos.TipoEnemigo;
import niveles.NivelBase;
import audios.EfectoSonido;
import audios.Musica; // si necesitas volumen

public class GestorCombate {

    // Procesa daño entre personaje <-> enemigos. No cambia flags de partida.
    public static void procesarCombate(Personaje personaje, NivelBase nivel, Musica musicaPartida) {
        Iterator<EnemigoBase> iter = nivel.getEnemigos().iterator();
        while (iter.hasNext()) {
            EnemigoBase e = iter.next();

            // MELEE del personaje sobre enemigo
            if (personaje.getTipoAtaque() == TipoAtaque.MELEE && personaje.getEstaAtacando()) {
                if (personaje.getHitbox().overlaps(e.getHitbox()) && e.getVida() > 0) {
                    e.reducirVida();
                    if (e.getVida() <= 0) {
                        nivel.agregarEnemigosMuertos(e);
                        e.remove();
                    }
                }
            }

            // DISTANCIA del personaje (balas del propio personaje)
            if (personaje.getTipoAtaque() == TipoAtaque.DISTANCIA) {
                Iterator<Proyectil> it = personaje.getBalas().iterator();
                while (it.hasNext()) {
                    Proyectil b = it.next();
                    if (b.getHitbox().overlaps(e.getHitbox()) && e.getVida() > 0) {
                        e.reducirVida();
                        b.desactivar();
                        if (e.getVida() <= 0) {
                            nivel.agregarEnemigosMuertos(e);
                            e.remove();
                        }
                    }
                }
            }

            // daño por contacto de enemigo perseguidor
            if (e.getTipoEnemigo() == TipoEnemigo.PERSEGUIDOR && e.getHitbox().overlaps(personaje.getHitbox())) {
                personaje.reducirVida();
            }

            // proyectiles enemigos sobre personaje
            Iterator<Proyectil> it2 = e.getBalas().iterator();
            while (it2.hasNext()) {
                Proyectil b = it2.next();
                if (b.getHitbox().overlaps(personaje.getHitbox())) {
                    if (personaje.getEstaAtacando() && personaje.getTipoAtaque() == TipoAtaque.MELEE) {
                        EfectoSonido.reproducir("Parry", musicaPartida.getVolumen());
                        b.desactivar();
                    } else {
                        personaje.reducirVida();
                        b.desactivar();
                    }
                }
            }
        }
    }
}
