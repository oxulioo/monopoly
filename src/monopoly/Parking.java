package monopoly;

import partida.Jugador;

public class Parking extends Accion {

    private int bote; // El valor acumulado

    public Parking(int posicion, int valorInicial) {
        super("Parking", posicion);
        this.bote = valorInicial;
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // LÓGICA MOVIDA DE CASILLA.JAVA (Caso Parking)
        if (this.bote > 0) {
            // Guardamos el valor para el mensaje antes de resetearlo
            int premio = this.bote;

            actual.sumarFortuna(premio);
            actual.getEstadisticas().sumarPremiosInversionesOBote(premio);
            this.bote = 0; // Reseteamos el bote

            System.out.println(actual.getNombre() + " cae en el Parking y se lleva el bote de " + premio + "€.");
        } else {
            System.out.println(actual.getNombre() + " cae en el Parking, pero el bote está a 0€.");
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
