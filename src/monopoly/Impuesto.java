package monopoly;

import partida.Jugador;

public class Impuesto extends Casilla {

    private int valor; // El impuesto definido

    public Impuesto(String nombre, int posicion, int valor) {
        super(nombre, Casilla.TIMPUESTO, posicion);
        this.valor = valor;
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // --- LÓGICA MOVIDA DE CASILLA.JAVA (Bloque TIMPUESTO) ---

        int cantidad = (this.valor > 0) ? this.valor : Valor.IMPUESTO_FIJO;
        System.out.println("Jugador actual: " + actual.getNombre() + " debe pagar: " + cantidad);

        // Cobrar al jugador
        boolean ok = actual.sumarGastos(cantidad);
        if (ok) {
            // Actualizar estadísticas
            actual.getEstadisticas().sumarPagoTasasImpuestos(cantidad);

            // Añadir al bote del Parking
            // Usamos la referencia estática que mantuvimos en la clase padre Casilla
            Casilla parking = Casilla.getParkingReferencia();
            if (parking != null) {
                parking.sumarValor(cantidad); // Esto funcionará cuando implementemos Parking
                // Nota: getValor() en Casilla devuelve 0 por defecto, pero Parking lo sobrescribirá
                System.out.println("Dinero añadido al parking. Bote actual: " + parking.getValor());
            }
        }
    }

    @Override
    public String toString() {
        // --- LÓGICA MOVIDA DE infoCasilla (Bloque impuesto) ---
        return "{\n"
                + "tipo: impuesto,\n"
                + "Tasa: " + this.valor + "\n"
                + "}";
    }

    // Sobrescribimos getValor por si se consulta desde fuera
    @Override
    public int getValor() {
        return this.valor;
    }
}