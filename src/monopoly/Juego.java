package monopoly;
import partida.*;
import java.util.ArrayList;

public class Juego {
    private final Jugador banca;  // Atributo de instancia
    private ArrayList<Jugador> jugadores;
    private Tablero tablero;

    public Juego() {
        this.banca = new Jugador(); // Constructor vacío = banca
        this.tablero = new Tablero(banca); // Se pasa al crear el tablero
        this.jugadores = new ArrayList<>();
    }

    //private long secEdificio = 1;
    //private long nextEdificioId() { return secEdificio++; }


    public Jugador getBanca() {
        return banca;
    }

    // Cuando necesitas la banca, la pasas como parámetro
    public boolean comprarPropiedad(String nombreCasilla) {
        Jugador actual = this.banca;
        Casilla cas = tablero.encontrar_casilla(nombreCasilla);

        // Pasas la banca a la casilla
        cas.comprarCasilla(actual, banca); // La pasas explícitamente

        return true;
    }
}
