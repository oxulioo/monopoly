package monopoly;

import partida.*;
import java.util.ArrayList;


class Grupo {

    // region ==== ATRIBUTOS ====

    private ArrayList<Casilla> miembros; //Casillas miembros del grupo.
    private String colorGrupo; //Color del grupo
    private int numCasillas; //Número de casillas del grupo.

    // endregion

    // region ==== CONSTRUCTORES ====

    public Grupo() {
        this.miembros=new ArrayList<>();
        this.colorGrupo="";
        this.numCasillas=0;
    }

    /*Constructor para cuando el grupo está formado por DOS CASILLAS:
    * Requiere como parámetros las dos casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, String colorGrupo) {
        this.miembros=new ArrayList<>();
        this.colorGrupo=colorGrupo;
        this.numCasillas=2;
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
    }

    /*Constructor para cuando el grupo está formado por TRES CASILLAS:
    * Requiere como parámetros las tres casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, Casilla cas3, String colorGrupo) {
        this.miembros=new ArrayList<>();
        this.colorGrupo=colorGrupo;
        this.numCasillas=3;
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
        anhadirCasilla(cas3);
    }

    /* Método que añade una casilla al array de casillas miembro de un grupo.
    * Parámetro: casilla que se quiere añadir.
     */
    public void anhadirCasilla(Casilla miembro) {
        if(miembro==null){
            return;
        }
        if(!Casilla.TSOLAR.equals(miembro.getTipo())){
            System.out.println("En este diseño solo tiene sentido agrupar solares");
            return;
        }
        if(!miembros.contains(miembro)){
            miembros.add(miembro);
            miembro.setGrupo(this);
        }

    }

    /*Método que comprueba si el jugador pasado tiene en su haber todas las casillas del grupo:
    * Parámetro: jugador que se quiere evaluar.
    * Valor devuelto: true si es dueño de todas las casillas del grupo, false en otro caso.
     */
    public boolean esDuenoGrupo(Jugador jugador) {
        if (jugador == null || miembros.size() != numCasillas || numCasillas == 0) {
            return false;
        }
        //Recorro todas las casillas
        for (Casilla c : miembros) {
            Jugador d = c.getDueno();
            if (d == null || d != jugador) {//Si d es null, es la banca y si es distinto de jugador, es distinto dueno
                return false;
            }
        }
        return true;
    }
    //Añado un getter del color
    public String getColorGrupo(){
        return colorGrupo;
    }

    // endregion

}
