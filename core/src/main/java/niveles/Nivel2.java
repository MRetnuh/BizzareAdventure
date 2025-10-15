package niveles;

import personajes.Enemigo;
import personajes.Personaje;

public class Nivel2 extends NivelBase {

    public Nivel2() {
        super("mapacorregido.tmx");
    }

    @Override
    public void definirPosicionesIniciales() {
        this.inicioX1 = 500;
        this.inicioY1 = 930;
        this.inicioX2 = 500;
        this.inicioY2 = 930;
    }

    @Override
    public void crearEnemigos() {
        this.enemigos.clear(); // Limpiar enemigos anteriores si se reutiliza la instancia

        String[] idsEnemigos = {"enemigo1", "enemigo2", "enemigo3"};
        float[][] posiciones = {
            {1000, 928},
            {1200, 928},
            {1400, 928}
        };

        for (int i = 0; i < idsEnemigos.length; i++) {
            String id = idsEnemigos[i];
            if (!NivelBase.enemigosMuertosGlobal.contains(id)) { 
                this.enemigos.add(new Enemigo(id, posiciones[i][0], posiciones[i][1]));
            }
        }
    }

    @Override
    public boolean comprobarVictoria(float nuevaX1, float nuevaY1, float nuevaX2, float nuevaY2) {
        // La condición de victoria es alcanzar un punto de salida
        return (nuevaX1 >= 3502.00 && nuevaX1 <= 3700.00 && nuevaY1 >= 1250.00) || 
               (nuevaX2 >= 3502.00 && nuevaX2 <= 3700.00 && nuevaY2 >= 1250.00);
    }
}