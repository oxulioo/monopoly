package monopoly.carta;

import monopoly.casilla.Casilla;
import monopoly.Juego;
import monopoly.casilla.Propiedad;
import monopoly.exceptions.AccionInvalidaException;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;
import monopoly.partida.Valor;

public class CartaSuerte extends Carta {

    public CartaSuerte(int id, String descripcion) {
        super(id, descripcion);
    }

    @Override
    public void accion(Jugador jugador, Juego juego) throws MonopolyEtseException {
        Juego.consola.imprimir("Carta de Suerte: " + descripcion);

        switch (id) {
            case 1: // Viaje a Solar19
                juego.moverJugadorACasilla(jugador, "Solar19", true);
                break;
            case 2: // A la cárcel
                juego.enviarACarcel(jugador);
                break;
            case 3: // Lotería
                jugador.sumarFortuna(1000000);
                jugador.getEstadisticas().sumarPremiosInversionesOBote(1000000);
                break;
            case 4: // Pagar a todos
                pagarATodos(jugador, juego, 250000);
                jugador.getEstadisticas().sumarPagoTasasImpuestos(250000L * juego.getJugadoresNum());
                break;
            case 5: // Retroceder 3
                retrocederCasillas(jugador, juego, 3);
                break;
            case 6: // Multa móvil
                pagarSiPuede(jugador, 150000);
                break;
            case 7: // Transporte cercano
                avanzarTransporteMasCercano(jugador, juego);
                break;
            default:
                Juego.consola.imprimir("Acción de suerte no definida para ID: " + id);
        }
    }

    private void pagarATodos(Jugador jugador, Juego juego, int cantidad) {
        for (Jugador otro : juego.getJugadores()) {
            if (!otro.equals(jugador) && !"Banca".equals(otro.getNombre())) {
                jugador.restarDinero(cantidad);
                otro.sumarFortuna(cantidad);
                otro.getEstadisticas().sumarCobroDeAlquileres(cantidad);
                // (Al que paga se le cuenta como tasas/impuestos al ser carta de suerte)
                jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
                Juego.consola.imprimir(jugador.getNombre() + " paga " + cantidad + "€ a " + otro.getNombre() + ".");
            }
        }
    }

    private void retrocederCasillas(Jugador jugador, Juego juego, int casillas) throws MonopolyEtseException {
        Casilla actual = jugador.getAvatar().getPosicion();
        int nuevaPos = (actual.getPosicion() - casillas - 1 + 40) % 40 + 1;
        juego.moverJugadorAPosicion(jugador, nuevaPos);
    }

    private void pagarSiPuede(Jugador jugador, int cantidad) {
        jugador.restarDinero(cantidad);
        jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
        Casilla parking = Casilla.getParkingReferencia();
        if (parking != null) parking.sumarValor(cantidad);
    }

    private void avanzarTransporteMasCercano(Jugador jugador, Juego juego) throws AccionInvalidaException {
        // Buscar primer transporte
        String[] transportes = {"Trans1", "Trans2", "Trans3", "Trans4"};
        for (String trans : transportes) {
            if (juego.getTablero().encontrar_casilla(trans) != null) {
                try {
                    juego.moverJugadorACasilla(jugador, trans, true);
                } catch (MonopolyEtseException e) {
                   throw new AccionInvalidaException("No se ha podido avanzar");
                }

                // Posiciones fijas de las casillas de Transporte
                final int[] posTransportes = {6, 16, 26, 36};
                final String[] nomTransportes = {"Trans1", "Trans2", "Trans3", "Trans4"};

                Casilla actual = jugador.getAvatar().getPosicion();
                int minDistancia = 1000; // iniciamos un número muy grande para que no se encuentre nunca
                String transporteMasCercano = nomTransportes[0];
                int posDestinoFinal = posTransportes[0];

                // 1. Calcular la casilla más cercana
                for (int i = 0; i < posTransportes.length; i++) {
                    int posDestino = posTransportes[i];
                    int distancia;

                    if (posDestino > actual.getPosicion()) {
                        distancia = posDestino - actual.getPosicion(); // Movimiento hacia adelante
                    } else {
                        distancia = (40 - actual.getPosicion()) + posDestino; // Dando la vuelta, menos o igual que donde estamos
                    }

                    if (distancia < minDistancia) {
                        minDistancia = distancia;
                        transporteMasCercano = nomTransportes[i];
                        posDestinoFinal = posDestino;
                    }
                }

                // 2. Mover al jugador y gestionar pago
                Propiedad destino = (Propiedad) juego.getTablero().encontrar_casilla(transporteMasCercano);
                if (destino == null) return;

                System.out.println("Avanzando al transporte más cercano: " + destino.getNombre());

                // 3. Comprobar si pasa por Salida (antes de mover)
                boolean cobraSalida = posDestinoFinal < actual.getPosicion();
                if (cobraSalida) {
                    jugador.sumarFortuna(Valor.SUMA_VUELTA);
                    jugador.getEstadisticas().sumarPasarPorSalida();
                    jugador.setVueltas(jugador.getVueltas() + 1);
                    System.out.println(jugador.getNombre() + " pasa por salida y recibe " + Valor.SUMA_VUELTA + "€.");
                }

                // 4. Mover el avatar (sin llamar a evaluarCasilla)
                jugador.getAvatar().setPosicion(destino);
                destino.incrementarVisita();

                // 5. Lógica de pago de la carta
                Jugador dueno = destino.getDueno();
                if (dueno != null && dueno != juego.getBanca() && dueno != jugador) {
                    // "paga al dueño el doble de la operación indicada"
                    System.out.println("La casilla pertenece a " + dueno.getNombre() + ". ¡Pagas el doble de alquiler!");

                    // Reutilizamos el método existente con factor 2
                    jugador.pagarAlquiler(destino, 2);

                } else if (dueno == null || dueno == juego.getBanca()) {
                    System.out.println("La casilla no tiene dueño. Puedes comprarla en tu turno.");
                }
            }
        }
    }
}
