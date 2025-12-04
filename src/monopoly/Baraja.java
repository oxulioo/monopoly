package monopoly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baraja {
    private final List<Carta> mazoSuerte;
    private final List<Carta> mazoComunidad;
    private int indiceSuerte = 0;
    private int indiceComunidad = 0;

    public Baraja() {
        this.mazoSuerte = new ArrayList<>();
        this.mazoComunidad = new ArrayList<>();
        inicializarCartas();
        barajar();
    }

    private void inicializarCartas() {
        // Cartas de Suerte
        mazoSuerte.add(new CartaSuerte(1, "Viaje de placer. Ve a Solar19..."));
        mazoSuerte.add(new CartaSuerte(2, "Te persiguen acreedores. Ve a la Cárcel..."));
        mazoSuerte.add(new CartaSuerte(3, "Lotería: 1.000.000€"));
        mazoSuerte.add(new CartaSuerte(4, "Presidente. Paga 250.000€ a cada uno"));
        mazoSuerte.add(new CartaSuerte(5, "Tráfico. Retrocede 3 casillas"));
        mazoSuerte.add(new CartaSuerte(6, "Multa móvil. Paga 150.000€"));
        mazoSuerte.add(new CartaSuerte(7, "Avanza al transporte más cercano..."));

        // Cartas de Comunidad
        mazoComunidad.add(new CartaCajaComunidad(1, "Balneario. Paga 500.000€"));
        mazoComunidad.add(new CartaCajaComunidad(2, "Fraude identidad. Ve a la Cárcel..."));
        mazoComunidad.add(new CartaCajaComunidad(3, "Ve a Salida. Cobra 2M"));
        mazoComunidad.add(new CartaCajaComunidad(4, "Hacienda devuelve 500.000€"));
        mazoComunidad.add(new CartaCajaComunidad(5, "Antigüedades. Ve a Solar1"));
        mazoComunidad.add(new CartaCajaComunidad(6, "San Fermín. Ve a Solar20"));
    }

    public void barajar() {
        Collections.shuffle(mazoSuerte);
        Collections.shuffle(mazoComunidad);
        indiceSuerte = 0;
        indiceComunidad = 0;
    }

    public Carta sacarCarta(String tipo) {
        if ("Suerte".equals(tipo)) {
            if (mazoSuerte.isEmpty()) return null;
            Carta c = mazoSuerte.get(indiceSuerte);
            indiceSuerte = (indiceSuerte + 1) % mazoSuerte.size(); // Rotación circular
            return c;
        } else if ("Comunidad".equals(tipo)) {
            if (mazoComunidad.isEmpty()) return null;
            Carta c = mazoComunidad.get(indiceComunidad);
            indiceComunidad = (indiceComunidad + 1) % mazoComunidad.size();
            return c;
        }
        return null;
    }
}