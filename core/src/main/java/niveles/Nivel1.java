package niveles;

import personajes.Enemigo;
import personajes.Personaje;

public class Nivel1 extends NivelBase {

    public Nivel1() {
        super("mapacorregido.tmx");
    }

    @Override
    public void definirPosicionesIniciales() {
        this.inicioX1 = 200;
        this.inicioY1 = 930;
        this.inicioX2 = 200;
        this.inicioY2 = 930;
    }

    @Override
    public void crearEnemigos() {
        this.enemigos.clear(); 

        String[] idsEnemigos = {"enemigo1"};
        float[][] posiciones = {
        		  {600, 2000}};

        for (int i = 0; i < idsEnemigos.length; i++) {
            String id = idsEnemigos[i];
            if (!NivelBase.enemigosMuertosGlobal.contains(id)) { 
                this.enemigos.add(new Enemigo(id, posiciones[i][0], posiciones[i][1]));
            }
        }
    }

    @Override
    public boolean comprobarVictoria(float nuevaX1, float nuevaY1, float nuevaX2, float nuevaY2) {
        return (nuevaX1 >= 3502.00 && nuevaX1 <= 3700.00 && nuevaY1 >= 1250.00) || 
               (nuevaX2 >= 3502.00 && nuevaX2 <= 3700.00 && nuevaY2 >= 1250.00);
    }
}