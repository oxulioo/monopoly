package monopoly;

import partida.Avatar;
import partida.Jugador;
import java.util.ArrayList;

public abstract class Casilla {

    // Atributos comunes a TODAS las casillas
    protected String nombre;
    protected int posicion;
    protected ArrayList<Avatar> avatares;
    protected int vecesVisitada; // Para estadísticas

    // Constructor base
    public Casilla(String nombre, int posicion) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.avatares = new ArrayList<>();
        this.vecesVisitada = 0;
    }

    // --- Métodos requeridos por el PDF (Requisito 26) ---

    // Devuelve true si el avatar está en esta casilla
    public boolean estaAvatar(Avatar avatar) {
        return avatares.contains(avatar);
    }

    // Devuelve la frecuencia de visita
    public int frecuenciaVisita() {
        return vecesVisitada;
    }

    // Método abstracto: cada tipo de casilla definirá su propia salida por pantalla
    @Override
    public abstract String toString();

    // --- Métodos comunes de gestión ---

    public void incrementarVisita() {
        this.vecesVisitada++;
    }

    public void anhadirAvatar(Avatar av) {
        if (av != null) {
            this.avatares.add(av);
        }
    }

    public void eliminarAvatar(Avatar av) {
        this.avatares.remove(av);
    }

    // Getters básicos
    public String getNombre() {
        return nombre;
    }

    public int getPosicion() {
        return posicion;
    }

    public ArrayList<Avatar> getAvatares() {
        return avatares;
    }

    // --- Método CLAVE para el polimorfismo ---
    // Sustituye al antiguo switch gigante. Cada subclase implementará su lógica aquí.
    public abstract void evaluarCasilla(Jugador actual, Juego juego, int tirada);

}