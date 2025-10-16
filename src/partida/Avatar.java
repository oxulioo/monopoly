package partida;

import monopoly.*;

import monopoly.Casilla;

import java.util.ArrayList;

public class Avatar {
    //A continuación, tenemos otros métodos útiles para el desarrollo del juego.
    /*Método que permite mover a un avatar a una casilla concreta. Parámetros:
    * - Un array con las casillas del tablero. Se trata de un arrayList de arrayList de casillas (uno por lado).
    * - Un entero que indica el numero de casillas a moverse (será el valor sacado en la tirada de los dados).
    * EN ESTA VERSIÓN SUPONEMOS QUE valorTirada siemrpe es positivo.
     */

    // ==== ATRIBUTOS ====
    private char id;
    private String tipo;
    private Casilla lugar;
    private Jugador jugador;
    private int nDobles;

    // ==== CONSTRUCTOR ====
    public Avatar(char id, String tipo, Casilla posicion, Jugador jugador) {
        this.id = id;
        this.tipo = tipo;
        this.lugar = posicion;
        this.jugador = jugador;
        this.nDobles = 0;
    }

    // ==== GETTERS Y SETTERS ====

    public char getID() {
        return id;
    }
    public String getTipo() {
        return tipo;
    }
    public Casilla getPosicion() {
        return lugar;
    }
    public Jugador getJugador() {
        return jugador;
    }
    public int getnDobles() {
        return nDobles;
    }
    public void setnDobles(int nDobles) {
        this.nDobles = nDobles;
    }
    public void setPosicion(Casilla posicion) {
        this.lugar = posicion;
    }



    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) { //pancho
        int totalPosiciones = 0;
        for (ArrayList<Casilla> fila: casillas) {
            totalPosiciones += fila.size();
        }

        int posActual= this.lugar.getPosicion();
        int nuevaPosicion = (posActual + valorTirada) % totalPosiciones;

        if(posActual + valorTirada >= totalPosiciones){
            jugador.sumarFortuna(2000000);
            jugador.setVueltas(jugador.getVueltas()+1);
            System.out.println("El jugador " + jugador.getNombre() + " pasa por salida y recibe 2.000.000€.");
        }

        Casilla nuevaCasilla = null;
        for (ArrayList<Casilla> fila: casillas) {
            for (Casilla casilla: fila) {
                if (casilla.getPosicion() == nuevaPosicion) {
                    nuevaCasilla = casilla;
                    break;
                }
            }
            if(nuevaCasilla != null){
                break;
            }
        }
        if(nuevaCasilla != null) {
            Casilla antiguaCasilla = this.lugar;
            this.lugar = nuevaCasilla;

            System.out.println("El avatar " + this.id + " avanza " + valorTirada + " casillas, llegando a la casilla " + nuevaCasilla.getNombre() + ".");
        }else {
            System.out.println("No se encuentra la casilla pedida");
        }
    }

    /*Método que permite generar un ID para un avatar. Sólo lo usamos en esta clase (por ello es privado).
    * El ID generado será una letra mayúscula. Parámetros:
    * - Un arraylist de los avatares ya creados, con el objetivo de evitar que se generen dos ID iguales.
     */
    private void generarId(ArrayList<Avatar> avCreados) { //pancho
        char id;
        do{
            id = generarLetraMayuscula();
        } while (existeId (avCreados, String.valueOf(id)));
        this.id = id;
        avCreados.add(this);
    }

    private boolean existeId(ArrayList<Avatar> avCreados, char id){ //pancho
        for (Avatar avatar : avCreados) {
            if (avatar.id.equals(id)) {
                return true;
            }
        }
        return false;

    }
    private char generarLetraMayuscula() { //pancho
        char letra= (char)(Math. random()*26+97); //le suma la letra 'A'
    }

}

