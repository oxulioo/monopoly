package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;

public class IrCarcel extends Especial {

    public IrCarcel(int posicion) {
        super("IrCarcel", posicion);
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();
        // Nota: El movimiento físico del avatar lo suele hacer Juego tras llamar a esto,
        // o si usas el método enviarACarcel de Juego que también mueve el avatar.
        juego.enviarACarcel(actual);
    }

    @Override
    public String toString() {
        return "{\n"
                + "tipo: especial,\n"
                + "nombre: " + nombre + "\n"
                + "}";
    }
}
