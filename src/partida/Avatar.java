package partida;

import monopoly.*;

import java.util.ArrayList;


//A continuación, tenemos otros métodos útiles para el desarrollo del juego.
    /*Método que permite mover a un avatar a una casilla concreta. Parámetros:
    * - Un array con las casillas del tablero. Se trata de un arrayList de arrayList de casillas (uno por lado).
    * - Un entero que indica el numero de casillas a moverse (será el valor sacado en la tirada de los dados).
    * EN ESTA VERSIÓN SUPONEMOS QUE valorTirada siemrpe es positivo.
     */
    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) { //pancho
        int totalPosiciones = 0;
        for (ArrayList<Casilla> fila: casillas){
        totalPosiciones += fila.size();
        }
        int posicionActual= this.lugar.getPosicion();
        int nuevaPosicion = (posicionActual + valorTirada)%totalPosiciones;
        for (int i=0; i<totalPosiciones; i++){
            ArrayList<Casilla> fila = casillas.get(i);
            for (int j=0; j< fila.size(); j++){
                Casilla casilla = fila.get(j);
                if  (casilla.getPosicion() == nuevaPosicion){
                //muevo el avatar a esta casilla
                }
            }
        }
    }

    /*Método que permite generar un ID para un avatar. Sólo lo usamos en esta clase (por ello es privado).
    * El ID generado será una letra mayúscula. Parámetros:
    * - Un arraylist de los avatares ya creados, con el objetivo de evitar que se generen dos ID iguales.
     */
    private void generarId(ArrayList<Avatar> avCreados) { //pancho
        String id="";
        do{
            id= ""+ generarLetraMayuscula()
        } while (existeId (avCreados, id));
        this.id = id;
        avCreados.add(this);
    }

    private boolean existeId(ArrayList<Avatar> avCreados, String id){ //pancho
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

    //Método para obtener el jugador al que pertenece el avatar
    public Jugador getJugador(){
        return jugador;
    }
}

