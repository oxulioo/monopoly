package monopoly.carta;

import monopoly.casilla.Casilla;
import monopoly.Juego;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;

import java.util.ArrayList;

public class CartaSuerte extends Carta {

    public CartaSuerte(int id, String descripcion) {
        super(id, descripcion);
    }



    @Override
    public void accion(Jugador jugador, Juego juego) throws MonopolyEtseException{
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
        // Obtenemos la lista de jugadores usando el método que acabamos de crear en Juego
        java.util.List<Jugador> jugadores = juego.getJugadores();

        for (Jugador otro : jugadores) {
            if (!otro.equals(jugador) && !otro.getNombre().equals("Banca")) {
                if (jugador.sumarGastos(cantidad)) {
                    jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
                    otro.sumarFortuna(cantidad);
                    otro.getEstadisticas().sumarCobroDeAlquileres(cantidad);
                    Juego.consola.imprimir(jugador.getNombre() + " paga " + cantidad + "€ a " + otro.getNombre());
                } else {
                    Juego.consola.imprimir(jugador.getNombre() + " no tiene dinero para pagar a " + otro.getNombre());
                }
            }
        }
    }

    private void retrocederCasillas(Jugador jugador, Juego juego, int casillas) throws MonopolyEtseException {
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

    private void avanzarTransporteMasCercano(Jugador jugador, Juego juego) throws MonopolyEtseException {
        Casilla actual;
        actual = jugador.getAvatar().getPosicion();
        int posActual = actual.getPosicion();

        // Las posiciones de los transportes son fijas en el tablero estándar
        int[] transportes = {6, 16, 26, 36};

        int mejorTransporte = -1;
        int menorDistancia = 100; // Un número grande inicial

        // 1. Calcular cuál es el más cercano (mirando en ambas direcciones)
        for (int t : transportes) {
            int distAdelante = (t - posActual + 40) % 40;
            int distAtras = (posActual - t + 40) % 40;
            int distanciaReal = Math.min(distAdelante, distAtras); // El que esté más cerca físicamente

            if (distanciaReal < menorDistancia) {
                menorDistancia = distanciaReal;
                mejorTransporte = t;
            }
        }

        Juego.consola.imprimir("El transporte más cercano está en la casilla " + mejorTransporte);

        // 2. "Avanzar" hasta él (siempre movimiento hacia adelante según reglas, pasando por Salida si hace falta)
        juego.moverJugadorAPosicion(jugador, mejorTransporte);

        // 3. Regla especial: Cobrar doble si tiene dueño
        Casilla nuevaCasilla = jugador.getAvatar().getPosicion();
        if (nuevaCasilla instanceof monopoly.casilla.Transporte trans) {
            Jugador dueno = trans.getDueno();

            // Si tiene dueño y no somos nosotros ni la banca
            if (dueno != null && !dueno.equals(jugador) && !dueno.getNombre().equals("Banca")) {
                Juego.consola.imprimir("Al llegar por Carta de Suerte, el alquiler es DOBLE.");
                jugador.pagarAlquiler(trans, 2); // Factor 2 para duplicar el pago
            }
        }
    }

}