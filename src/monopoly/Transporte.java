package monopoly;

import partida.Jugador;

public class Transporte extends Propiedad {

    public Transporte(String nombre, int posicion, int valor, int hipoteca, Jugador dueno) {
        super(nombre, posicion, valor, hipoteca, dueno);
    }

    @Override
    public boolean alquiler(Jugador actual) {
        // LÓGICA MOVIDA DE CASILLA.JAVA (Caso TTRANSPORTE)
        // En tu código original: actual.pagarAlquiler(this, 1);
        actual.pagarAlquiler(this, 1);
        return true;
    }

    @Override
    public float valor() {
        return (float)this.valor;
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