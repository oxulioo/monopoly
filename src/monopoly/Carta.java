package monopoly;

import partida.Jugador;
import java.util.ArrayList;
import java.util.List;

public class Carta {
    private int id;
    private String descripcion;
    private String tipo; // "Suerte" o "Comunidad"

    // Mazos estáticos para no tener que crearlos cada vez
    private static List<Carta> mazoSuerte = new ArrayList<>();
    private static List<Carta> mazoComunidad = new ArrayList<>();
    private static int indiceSuerte = 0;
    private static int indiceComunidad = 0;

    static {
        // Inicializar mazo de Suerte (una sola vez)
        mazoSuerte.add(new Carta(1, "Suerte", "Decides hacer un viaje de placer. Avanza hasta Solar19. Si pasas por la casilla de Salida, cobra 2.000.000€."));
        mazoSuerte.add(new Carta(2, "Suerte", "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€."));
        mazoSuerte.add(new Carta(3, "Suerte", "¡Has ganado el bote de la lotería! Recibe 1.000.000€."));
        mazoSuerte.add(new Carta(4, "Suerte", "Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€."));
        mazoSuerte.add(new Carta(5, "Suerte", "¡Hora punta de tráfico! Retrocede tres casillas."));
        mazoSuerte.add(new Carta(6, "Suerte", "Te multan por usar el móvil mientras conduces. Paga 150.000€."));
        mazoSuerte.add(new Carta(7, "Suerte", "Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga al dueño el doble de la operación indicada."));

        // Inicializar mazo de Comunidad
        mazoComunidad.add(new Carta(1, "Comunidad", "Paga 500.000€ por un fin de semana en un balneario de 5 estrellas."));
        mazoComunidad.add(new Carta(2, "Comunidad", "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€."));
        mazoComunidad.add(new Carta(3, "Comunidad", "Colócate en la casilla de Salida. Cobra 2.000.000€."));
        mazoComunidad.add(new Carta(4, "Comunidad", "Devolución de Hacienda. Cobra 500.000€."));
        mazoComunidad.add(new Carta(5, "Comunidad", "Retrocede hasta Solar1 para comprar antigüedades exóticas."));
        mazoComunidad.add(new Carta(6, "Comunidad", "Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€."));
    }

    public Carta(int id, String tipo, String descripcion) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // Método estático para sacar carta (más simple)
    public static Carta sacarCarta(String tipo) {
        if ("Suerte".equals(tipo)) {
            Carta carta = mazoSuerte.get(indiceSuerte);
            indiceSuerte = (indiceSuerte + 1) % mazoSuerte.size();
            return carta;
        } else if ("Comunidad".equals(tipo)) {
            Carta carta = mazoComunidad.get(indiceComunidad);
            indiceComunidad = (indiceComunidad + 1) % mazoComunidad.size();
            return carta;
        }
        return null;
    }

    // Ejecutar la acción de la carta
    public void ejecutar(Jugador jugador, Juego juego) {
        System.out.println(jugador.getNombre() + ", elige una carta: " + id);
        System.out.println("Acción: " + descripcion);

        if ("Suerte".equals(tipo)) {
            ejecutarSuerte(jugador, juego);
        } else {
            ejecutarComunidad(jugador, juego);
        }
    }

    private void ejecutarSuerte(Jugador jugador, Juego juego) {
        switch(id) {
            case 1: avanzarACasilla(jugador, juego, "Solar19", true); break;
            case 2: juego.enviarACarcel(jugador); break;
            case 3: jugador.sumarFortuna(1000000); break;
            case 4: pagarATodos(jugador, juego, 250000); break;
            case 5: retrocederCasillas(jugador, juego, 3); break;
            case 6: pagarSiPuede(jugador, 150000); break;
            case 7: avanzarTransporteMasCercano(jugador, juego); break;
        }
    }

    private void ejecutarComunidad(Jugador jugador, Juego juego) {
        switch(id) {
            case 1: pagarSiPuede(jugador, 500000); break;
            case 2: juego.enviarACarcel(jugador); break;
            case 3:
                avanzarACasilla(jugador, juego, "Salida", false);
                jugador.sumarFortuna(2000000);
                break;
            case 4: jugador.sumarFortuna(500000); break;
            case 5: avanzarACasilla(jugador, juego, "Solar1", false); break;
            case 6: avanzarACasilla(jugador, juego, "Solar20", true); break;
        }
    }

    // Métodos auxiliares simples
    private void avanzarACasilla(Jugador jugador, Juego juego, String casilla, boolean cobrarSalida) {
        juego.moverJugadorACasilla(jugador, casilla, cobrarSalida);
    }

    private void pagarATodos(Jugador jugador, Juego juego, int cantidad) {
        for (Jugador otro : juego.getJugadores()) {
            if (otro != jugador && !"Banca".equals(otro.getNombre())) {
                if (jugador.getFortuna() >= cantidad) {
                    jugador.sumarGastos(cantidad);
                    otro.sumarFortuna(cantidad);
                }
            }
        }
    }

    private void retrocederCasillas(Jugador jugador, Juego juego, int casillas) {
        // Implementación simple - retrocede en el tablero
        Casilla actual = jugador.getAvatar().getPosicion();
        int nuevaPos = (actual.getPosicion() - casillas - 1 + 40) % 40 + 1;
        juego.moverJugadorAPosicion(jugador, nuevaPos);
    }

    private void avanzarTransporteMasCercano(Jugador jugador, Juego juego) {
        // Buscar primer transporte
        String[] transportes = {"Trans1", "Trans2", "Trans3", "Trans4"};
        for (String trans : transportes) {
            if (juego.getTablero().encontrar_casilla(trans) != null) {
                avanzarACasilla(jugador, juego, trans, true);
                break;
            }
        }
    }

    private void pagarSiPuede(Jugador jugador, int cantidad) {
        if (jugador.getFortuna() >= cantidad) {
            jugador.sumarGastos(cantidad);
        } else {
            System.out.println(jugador.getNombre() + " no tiene suficiente dinero. Debe hipotecar propiedades.");
        }
    }

    // Getters
    public String getDescripcion() { return descripcion; }
    public int getId() { return id; }
    public String getTipo() { return tipo; }
}