package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;

public class Suerte extends Accion {

    public Suerte(int posicion) {
        super("Suerte", posicion);
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();
        Juego.consola.imprimir(actual.getNombre() + " cae en Suerte.");
        // Delegamos en Juego la gestión de la carta, tal como tenías
        juego.procesarCasillaEspecial(actual, "Suerte");
    }

    @Override
    public String toString() {
        return "{\n" +
                "tipo: suerte\n" +
                "}";
    }
}
