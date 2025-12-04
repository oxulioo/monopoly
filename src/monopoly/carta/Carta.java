package monopoly.carta;

import monopoly.Juego;
import monopoly.jugador.Jugador;

public abstract class Carta {
    protected final int id;
    protected final String descripcion;

    public Carta(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }

    // Método abstracto: cada hija definirá qué hace
    public abstract void accion(Jugador jugador, Juego juego);
}