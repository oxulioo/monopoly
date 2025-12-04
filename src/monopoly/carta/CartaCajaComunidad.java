package monopoly.carta;

import monopoly.casilla.Casilla;
import monopoly.Juego;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;

public class CartaCajaComunidad extends Carta {

    public CartaCajaComunidad(int id, String descripcion) {
        super(id, descripcion);
    }

    @Override
    public void accion(Jugador jugador, Juego juego) throws MonopolyEtseException {
        Juego.consola.imprimir("Carta de Comunidad: " + descripcion);

        switch(id) {
            case 1: // Balneario
                pagarSiPuede(jugador, 500000);
                break;
            case 2: // Cárcel
                juego.enviarACarcel(jugador);
                break;
            case 3: // Salida
                juego.moverJugadorACasilla(jugador, "Salida", true);
                jugador.getEstadisticas().sumarPasarPorSalida();
                break;
            case 4: // Hacienda
                jugador.sumarFortuna(500000);
                jugador.getEstadisticas().sumarPremiosInversionesOBote(500000);
                break;
            case 5: // Solar1
                juego.moverJugadorACasilla(jugador, "Solar1", false);
                break;
            case 6: // Solar20
                juego.moverJugadorACasilla(jugador, "Solar20", true);
                break;
            default:
                Juego.consola.imprimir("Acción de comunidad no definida para ID: " + id);
        }
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
}