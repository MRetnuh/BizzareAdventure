package eventos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmisorEventos {
    private static final EmisorEventos instancia = new EmisorEventos();
    private final Map<String, List<Runnable>> oyentes = new HashMap<>();

    private EmisorEventos() {}

    public static EmisorEventos obtenerInstancia() {
        return instancia;
    }

    public void en(String evento, Runnable oyente) {
        oyentes.computeIfAbsent(evento, k -> new ArrayList<>()).add(oyente);
    }

    public void emitir(String evento) {
        List<Runnable> oyentesEvento = oyentes.get(evento);
        if (oyentesEvento != null) {
            for (Runnable oyente : oyentesEvento) {
                oyente.run();
            }
        }
    }
}