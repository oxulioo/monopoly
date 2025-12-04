package monopoly.carta;

import monopoly.casilla.Casilla;
import monopoly.Juego;
import monopoly.jugador.Jugador;

public class CartaSuerte extends Carta {

    public CartaSuerte(int id, String descripcion) {
        super(id, descripcion);
    }

    @Override
    public void accion(Jugador jugador, Juego juego) {
        Juego.consola.imprimir("Carta de Suerte: " + descripcion);

        switch(id) {
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
                // Nota: Asumimos que juego.getJugadoresNum() existe, si no usa juego.getJugadores().size()
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

    // --- Métodos privados copiados de tu antigua lógica ---

    private void pagarATodos(Jugador jugador, Juego juego, int cantidad) {
        // Tu lógica de iterar jugadores y pagar
        // Ejemplo simplificado:
        /*
        for (Jugador otro : juego.getJugadores()) {
            if (otro != jugador && !"Banca".equals(otro.getNombre())) {
                if (jugador.sumarGastos(cantidad)) {
                    otro.sumarFortuna(cantidad);
                    otro.getEstadisticas().sumarCobroDeAlquileres(cantidad);
                }
            }
        }
        */
        // IMPLEMENTA TU LÓGICA AQUÍ
    }

    private void retrocederCasillas(Jugador jugador, Juego juego, int casillas) {
        Casilla actual = jugador.getAvatar().getPosicion();
        int nuevaPos = (actual.getPosicion() - casillas - 1 + 40) % 40 + 1;
        juego.moverJugadorAPosicion(jugador, nuevaPos);
    }

    private void pagarSiPuede(Jugador jugador, int cantidad) {
        if (jugador.sumarGastos(cantidad)) {
            jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
            Casilla parking = Casilla.getParkingReferencia();
            if (parking != null) parking.sumarValor(cantidad);
        } else {
            Juego.consola.imprimir(jugador.getNombre() + " no tiene suficiente dinero.");
        }
    }

    private void avanzarTransporteMasCercano(Jugador jugador, Juego juego) {
        // PEGA AQUÍ TU MÉTODO DE TRANSPORTE COMPLETO
    }
}