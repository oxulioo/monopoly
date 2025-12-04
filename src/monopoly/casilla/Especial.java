package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;
import monopoly.exceptions.MonopolyEtseException; // <--- 1. AÑADIR IMPORT

public abstract class Especial extends Casilla {

    public Especial(String nombre, int posicion) {
        super(nombre, Casilla.TESPECIAL, posicion);
    }

    @Override
    // 2. AÑADIR throws MonopolyEtseException
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) throws MonopolyEtseException {
        // Método vacío por defecto
    }
}