package monopoly;

import partida.Jugador;

public class Servicio extends Propiedad {

    public Servicio(String nombre, int posicion, int valor, int hipoteca, Jugador dueno) {
        super(nombre, posicion, valor, hipoteca, dueno);
    }

    @Override
    public boolean alquiler(Jugador j) {
        // Este método se define por herencia, pero en Servicio la lógica depende de la tirada.
        // Por tanto, la lógica real está en evaluarCasilla.
        return false;
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // LÓGICA MOVIDA DE CASILLA.JAVA (Caso TSERVICIOS)

        // 1. Si tiene dueño y no soy yo -> Pagar
        if (this.dueno != null && !this.dueno.equals(actual) && !this.dueno.getNombre().equals("Banca")) {
            int factor_pago = 4 * tirada; // Usamos la tirada (suma) pasada como argumento
            actual.pagarAlquiler(this, factor_pago);
        }
        // 2. Si no tiene dueño -> Info (como en Propiedad)
        else if (dueno == null || dueno.getNombre().equals("Banca")) {
            System.out.println("Estás en " + nombre + ". Pertenece a la Banca.");
            System.out.println("Valor de compra: " + valor);
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