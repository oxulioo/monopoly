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

    // region ==== ATRIBUTOS ====

    private char id;
    private String tipo;
    private Casilla lugar;
    private Jugador jugador;
    private int nDobles;

    // endregion

    // region ==== CONSTRUCTOR ====

    public Avatar(String tipo, Casilla posicion, Jugador jugador, ArrayList<Avatar> avcreados) {
        this.tipo = tipo;
        this.lugar = posicion;
        try { if (posicion != null) posicion.anhadirAvatar(this); } catch (Throwable ignored) {}
        this.jugador = jugador;
        this.nDobles = 0;
        this.generarId(avcreados);
    }

    // endregion

    // region ==== GETTERS Y SETTERS ====

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
    /*public void setPosicion(Casilla posicion) {
        this.lugar = posicion;
    }
    */
    //XULIAN, HE CAMBIADO SETPOSICION PORQUE NO SE MOVIA BIEN EL AVATAR
    public void setPosicion(Casilla posicion) {
        if (this.lugar == posicion) return; // nada que hacer
        // quitar de la casilla anterior
        try {
            if (this.lugar != null) this.lugar.eliminarAvatar(this);
        } catch (Throwable ignored) {}
        // poner nueva
        this.lugar = posicion;
        // añadir a la casilla nueva (una sola vez)
        try {
            if (this.lugar != null) this.lugar.anhadirAvatar(this);
        } catch (Throwable ignored) {}
    }


    // endregion

    // region ==== MÉTODOS ====

    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) { //pancho

        //Comprobación previa
        if(casillas==null||this.lugar==null){
            System.out.println("No se puede mover el avatar, el tablero o la posición es nulo");
            return;
        }

        int totalPosiciones = 0;
        for (ArrayList<Casilla> fila: casillas) {
            totalPosiciones += fila.size();
        }
        if(totalPosiciones==0) return;


        int posActual= this.lugar.getPosicion()-1;
        int nuevaPosicion = (posActual + Math.max(0,valorTirada)) % totalPosiciones;//Evito que se realicen tiradas negativas con Math.max

        if(posActual + Math.max(0,valorTirada) >= totalPosiciones){
            jugador.sumarFortuna(Valor.SUMA_VUELTA);//Uso la constante definida para la vuelta
            jugador.setVueltas(jugador.getVueltas()+1);
            System.out.println("El jugador " + jugador.getNombre() + " pasa por salida y recibe 2.000.000€.");
        }
        int nuevaPosicionActual=nuevaPosicion+1;
        Casilla nuevaCasilla = null;
        for (ArrayList<Casilla> fila: casillas) {
            for (Casilla casilla: fila) {
                if (casilla!=null && casilla.getPosicion() == nuevaPosicionActual){ //Compruebo que la casilla no es null tampoco
                    nuevaCasilla = casilla;
                    break;
                }
            }
            if(nuevaCasilla != null){
                break;
            }
        }
        if(nuevaCasilla != null) {
            //Omito lo siguiente: Casilla antiguaCasilla = this.lugar;
            //Actualizo la lista de avatares (para imprimir en el tablero)
            setPosicion(nuevaCasilla);
            System.out.println("El avatar " + this.id + " avanza " + valorTirada + " casillas, llegando a la casilla " + nuevaCasilla.getNombre() + ".");
        }else {
            System.out.println("No se encuentra la casilla pedida");
        }
    }

    // endregion

    // region ==== GENERADOR DE ID ====
    private void generarId(ArrayList<Avatar> avCreados) {
        char id;
        do {
            id = generarLetraMayuscula();
        } while (existeId(avCreados, id));
        this.id = id;
        avCreados.add(this);
    }

    private boolean existeId(ArrayList<Avatar> avCreados, char id) {
        for (Avatar avatar : avCreados) {
            if (avatar.id == id) {
                return true;
            }
        }
        return false;
    }

    private char generarLetraMayuscula() {
        // ✅ devuelve A–Z correctamente
        char letra = (char) (Math.random() * 26 + 65);
        return letra;
    }

    // endregion

}

/*ESTO ES LO QUE TENÍAS ANTES XULIAN
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
 */
