package mecanicas;

import com.badlogic.gdx.math.Rectangle;

import jugadores.Jugador;
import niveles.NivelBase;
import personajes.Personaje;

public class GestorMovimiento {

    public static void aplicarMovimiento(Personaje personaje, float delta, NivelBase nivel, 
                                         Jugador[] jugadores, int jugador1Index, int jugador2Index, 
                                         boolean esJugador1, float MAX_DISTANCIA_X, float MAX_DISTANCIA_Y) {
        
        float nuevaX = personaje.getNuevaX(delta);
        float nuevaY = personaje.getNuevaY(delta);

        Personaje otroPersonaje = esJugador1 
                ? jugadores[jugador2Index].getPersonajeElegido() 
                : jugadores[jugador1Index].getPersonajeElegido();

        if (otroPersonaje != null && otroPersonaje.getVida() > 0) {
            float centroPersonajeX = nuevaX + personaje.getWidth() / 2f;
            float centroOtroX = otroPersonaje.getX() + otroPersonaje.getWidth() / 2f;
            float distanciaX = Math.abs(centroPersonajeX - centroOtroX);

            float centroPersonajeY = nuevaY + personaje.getHeight() / 2f;
            float centroOtroY = otroPersonaje.getY() + otroPersonaje.getHeight() / 2f;
            float distanciaY = Math.abs(centroPersonajeY - centroOtroY);

            if (distanciaX > MAX_DISTANCIA_X) nuevaX = personaje.getX();
            if (distanciaY > MAX_DISTANCIA_Y) nuevaY = personaje.getY();
        }

        Rectangle hitboxTentativaX = new Rectangle(personaje.getHitbox());
        hitboxTentativaX.setPosition(nuevaX, personaje.getY());
        boolean colisionX = nivel.detectarColision(hitboxTentativaX);

        Rectangle hitboxTentativaY = new Rectangle(personaje.getHitbox());
        hitboxTentativaY.setPosition(personaje.getX(), nuevaY);
        boolean colisionY = nivel.detectarColision(hitboxTentativaY);

        if (colisionY) {
            personaje.setVelocidadCaida(0);
            personaje.setY(personaje.getPrevY());
        }

        if (!colisionX || !colisionY) {
            float finalX = !colisionX ? nuevaX : personaje.getX();
            float finalY = !colisionY ? nuevaY : personaje.getY();
            personaje.aplicarMovimiento(finalX, finalY, delta, nivel.getAnchoMapa(), nivel.getAlturaMapa());
        }
    }
}
