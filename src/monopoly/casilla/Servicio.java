package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;

public class Servicio extends Propiedad {

    public Servicio(String nombre, int posicion, int valor, int hipoteca, Jugador dueno) {
        // AÑADIMOS Casilla.TSERVICIOS
        super(nombre, Casilla.TSERVICIOS, posicion, valor, hipoteca, dueno);
    }

    @Override
    public boolean alquiler(Jugador j) {
        // Este método se define por herencia, pero en Servicio la lógica depende de la tirada.
        // Por tanto, la lógica real está en evaluarCasilla.
        return false;
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();

        if(this.dueno != null && !this.dueno.equals(actual) && !this.dueno.getNombre().equals("Banca")){
            int factor_pago = 4 * tirada; // Usamos la tirada
            actual.pagarAlquiler(this, factor_pago);
        }
        else if (dueno == null || dueno.getNombre().equals("Banca")) {
            Juego.consola.imprimir("Estás en " + nombre + ". Valor: " + valor);
        }
    }

    @Override
    public float valor() {
        return (float)this.valor;
    }

    @Override
    public String toString() {
        // LÓGICA MOVIDA DE infoCasilla (Caso servicios)
        return "{\n"
                + "tipo: servicios,\n"
                + "valor: " + this.valor + "\n"
                + "}";
    }
}