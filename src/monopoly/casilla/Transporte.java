package monopoly.casilla;

import monopoly.jugador.Jugador;

public class Transporte extends Propiedad {

    public Transporte(String nombre, int posicion, int valor, int hipoteca, Jugador dueno) {

        super(nombre, Casilla.TTRANSPORTE, posicion, valor, hipoteca, dueno);
    }

    @Override
    public boolean alquiler(Jugador actual) {
        // LÓGICA MOVIDA DE CASILLA.JAVA (Caso TTRANSPORTE)
        // En tu código original: actual.pagarAlquiler(this, 1);
        actual.pagarAlquiler(this, 1);
        return true;
    }

    @Override
    public int valor() {
        return this.valor;
    }

    @Override
    public String toString() {
        // LÓGICA MOVIDA DE infoCasilla (Caso transporte)
        return "{\n"
                + "tipo: transporte,\n"
                + "valor: " + this.valor + "\n"
                + "}";
    }
}