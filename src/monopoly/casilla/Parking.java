package monopoly.casilla;

import monopoly.Juego;
import monopoly.carta.Accion;
import monopoly.jugador.Jugador;

public class Parking extends Accion {

    private int bote; // El valor acumulado

    public Parking(int posicion, int valorInicial) {
        super("Parking", posicion);
        this.bote = valorInicial;
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();
        // TU LÓGICA EXACTA DEL BLOQUE PARKING
        if(this.getValor() > 0){ // getValor() devuelve el bote,, SINO THIS.BOTE
            int premio = this.getValor();
            actual.sumarFortuna(premio);
            actual.getEstadisticas().sumarPremiosInversionesOBote(premio);
            // Resetear bote (necesitas un método para esto o setearlo a 0 si tienes acceso)
            // Como el atributo bote es privado en Parking, lo manejamos aquí:
            this.bote = 0;

            Juego.consola.imprimir(actual.getNombre() + " cae en el Parking y se lleva el bote de " + premio + "€.");
        } else {
            Juego.consola.imprimir(actual.getNombre() + " cae en el Parking, pero el bote está a 0€.");
        }
    }

    // --- MÉTODOS PARA GESTIONAR EL BOTE ---
    // Impuesto.java llamará a estos métodos a través de la referencia estática en Casilla

    @Override
    public void sumarValor(int suma) {
        this.bote += suma;
    }

    @Override
    public int getValor() {
        return this.bote;
    }

    @Override
    public String toString() {
        // LÓGICA MOVIDA DE infoCasilla (Caso Parking)
        StringBuilder jugadores = new StringBuilder();
        if (this.avatares != null && !this.avatares.isEmpty()) {
            for (int i = 0; i < this.avatares.size(); i++) {
                Jugador j = this.avatares.get(i).getJugador();
                String nom = (j == null) ? "-" : j.getNombre();
                if (i > 0) jugadores.append(", ");
                jugadores.append(nom);
            }
        }

        return "{\n"
                + "bote: " + this.bote + ",\n"
                + "jugadores: [" + jugadores + "]\n"
                + "}";
    }
}
