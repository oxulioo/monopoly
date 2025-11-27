package monopoly;

import partida.Jugador;

public abstract class Especial extends Casilla {

    public Especial(String nombre, int posicion) {
        super(nombre, Casilla.TESPECIAL, posicion);
    }

    // Comportamiento por defecto: no hace nada al caer (salvo que la hija diga lo contrario)
    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // Por defecto las especiales (como Salida o Visita Cárcel) no hacen nada al caer
        // La lógica de cobrar salida o estar preso se gestiona en el movimiento o estado del jugador
        return;
    }
}