package monopoly;

import partida.Jugador;
import java.util.ArrayList;
import java.util.List;

public class Carta {
    private final int id;
    private final String descripcion;
    private final String tipo;

    private static final List<Carta> mazoSuerte = new ArrayList<>();
    private static final List<Carta> mazoComunidad = new ArrayList<>();
    private static int indiceSuerte = 0;
    private static int indiceComunidad = 0;

    static {
        mazoSuerte.add(new Carta(1, "Suerte", "Decides hacer un viaje de placer. Avanza hasta Solar19. Si pasas por la casilla de Salida, cobra 2.000.000€."));
        mazoSuerte.add(new Carta(2, "Suerte", "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€."));
        mazoSuerte.add(new Carta(3, "Suerte", "¡Has ganado el bote de la lotería! Recibe 1.000.000€."));
        mazoSuerte.add(new Carta(4, "Suerte", "Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€."));
        mazoSuerte.add(new Carta(5, "Suerte", "¡Hora punta de tráfico! Retrocede tres casillas."));
        mazoSuerte.add(new Carta(6, "Suerte", "Te multan por usar el móvil mientras conduces. Paga 150.000€."));
        mazoSuerte.add(new Carta(7, "Suerte", "Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga al dueño el doble de la operación indicada."));

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

    public void ejecutar(Jugador jugador, Juego juego) {
        Juego.consola.imprimir(jugador.getNombre() + ", elige una carta: " + id);
        Juego.consola.imprimir("Acción: " + descripcion);

        if ("Suerte".equals(tipo)) {
            ejecutarSuerte(jugador, juego);
        } else {
            ejecutarComunidad(jugador, juego);
        }
    }

    private void ejecutarSuerte(Jugador jugador, Juego juego) {
        switch(id) {
            case 1: avanzarACasilla(jugador, juego, "Solar19", true);break;
            case 2: juego.enviarACarcel(jugador); break;
            case 3: jugador.sumarFortuna(1000000);
                jugador.getEstadisticas().sumarPremiosInversionesOBote(1000000);
                break;
            case 4: pagarATodos(jugador, juego, 250000);
                jugador.getEstadisticas().sumarPagoTasasImpuestos(250000L *juego.getJugadoresNum());
                break;
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
                jugador.getEstadisticas().sumarPasarPorSalida(2000000);
                break;
            case 4: jugador.sumarFortuna(500000);
                jugador.getEstadisticas().sumarPremiosInversionesOBote(500000);
                break;
            case 5: avanzarACasilla(jugador, juego, "Solar1", false); break;
            case 6: avanzarACasilla(jugador, juego, "Solar20", true); break;
        }
    }

    private void avanzarACasilla(Jugador jugador, Juego juego, String casilla, boolean cobrarSalida) {
        juego.moverJugadorACasilla(jugador, casilla, cobrarSalida);
    }

    private void pagarATodos(Jugador jugador, Juego juego, int cantidad) {
        for (Jugador otro : juego.getJugadores()) {
            if (otro != jugador && !"Banca".equals(otro.getNombre())) {
                boolean ok = jugador.sumarGastos(cantidad);
                if (ok) {
                    otro.sumarFortuna(cantidad);
                    otro.getEstadisticas().sumarCobroDeAlquileres(cantidad);
                }
            }
        }
    }

    private void retrocederCasillas(Jugador jugador, Juego juego, int casillas) {
        Casilla actual = jugador.getAvatar().getPosicion();
        int nuevaPos = (actual.getPosicion() - casillas - 1 + 40) % 40 + 1;
        juego.moverJugadorAPosicion(jugador, nuevaPos);
    }

    private void avanzarTransporteMasCercano(Jugador jugador, Juego juego) {
        final int[] posTransportes = {6, 16, 26, 36};
        final String[] nomTransportes = {"Trans1", "Trans2", "Trans3", "Trans4"};

        Casilla actual = jugador.getAvatar().getPosicion();
        int minDistancia = 1000;
        String transporteMasCercano = nomTransportes[0];
        int posDestinoFinal = posTransportes[0];

        for (int i = 0; i < posTransportes.length; i++) {
            int posDestino = posTransportes[i];
            int distancia;
            if (posDestino > actual.getPosicion()) {
                distancia = posDestino - actual.getPosicion();
            } else {
                distancia = (40 - actual.getPosicion()) + posDestino;
            }
            if (distancia < minDistancia) {
                minDistancia = distancia;
                transporteMasCercano = nomTransportes[i];
                posDestinoFinal = posDestino;
            }
        }

        Casilla destino = juego.getTablero().encontrar_casilla(transporteMasCercano);
        if (destino == null) return;

        Juego.consola.imprimir("Avanzando al transporte más cercano: " + destino.getNombre());

        boolean cobraSalida = posDestinoFinal < actual.getPosicion();
        if (cobraSalida) {
            jugador.sumarFortuna(Valor.SUMA_VUELTA);
            jugador.getEstadisticas().sumarPasarPorSalida(Valor.SUMA_VUELTA);
            jugador.setVueltas(jugador.getVueltas() + 1);
            Juego.consola.imprimir(jugador.getNombre() + " pasa por salida y recibe " + Valor.SUMA_VUELTA + "€.");
        }

        jugador.getAvatar().setPosicion(destino);
        destino.incrementarVisita();

        // CORRECCIÓN: Casteo a Propiedad para ver dueño
        if (destino instanceof Propiedad) {
            Jugador dueno = ((Propiedad)destino).getDueno();
            if (dueno != null && dueno != juego.getBanca() && dueno != jugador) {
                Juego.consola.imprimir("La casilla pertenece a " + dueno.getNombre() + ". ¡Pagas el doble de alquiler!");
                jugador.pagarAlquiler(destino, 2);
            } else if (dueno == null || dueno == juego.getBanca()) {
                Juego.consola.imprimir("La casilla no tiene dueño. Puedes comprarla en tu turno.");
            }
        }
    }

    private void pagarSiPuede(Jugador jugador, int cantidad) {
        boolean ok = jugador.sumarGastos(cantidad);
        if (ok) {
            jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
            Casilla parking = Casilla.getParkingReferencia();
            if (parking != null) parking.sumarValor(cantidad);
        } else {
            Juego.consola.imprimir(jugador.getNombre() + " no tiene suficiente dinero. Debe hipotecar propiedades.");
        }
    }
}