package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;

public abstract class Especial extends Casilla {

    public Especial(String nombre, int posicion) {
        super(nombre, Casilla.TESPECIAL, posicion);
    }

    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {}
}