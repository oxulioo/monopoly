package monopoly.casilla;

import monopoly.Juego;
import monopoly.carta.Accion;
import monopoly.jugador.Jugador;

public class CajaComunidad extends Accion {

    public CajaComunidad(int posicion) {
        super("Caja", posicion); // Mantenemos el nombre "Caja" para coincidir con tu lógica de Tablero/Carta
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();
        Juego.consola.imprimir(actual.getNombre() + " cae en Caja de Comunidad.");
        // Delegamos en Juego la gestión de la carta
        juego.procesarCasillaEspecial(actual, "Comunidad");
    }

    @Override
    public String toString() {
        return "{\n" +
                "tipo: comunidad\n" +
                "}";
    }
}
