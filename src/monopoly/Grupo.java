package monopoly;

import partida.*;
import java.util.ArrayList;


class Grupo {

    // region ==== ATRIBUTOS ====

    private final ArrayList<Casilla> miembros; //Casillas miembros del grupo.
    private final String colorGrupo; //Color del grupo
    private final int numCasillas; //Número de casillas del grupo.

    // endregion

    // region ==== CONSTRUCTORES ====

    public Grupo() {
        //Inicializo con estos valores (defensivo)
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

    // endregion

    // region ==== MÉTODOS ====

    /* Método que añade una casilla al array de casillas miembro de un grupo.
    * Parámetro: casilla que se quiere añadir.
     */
    public void anhadirCasilla(Casilla miembro) {
        //Compruebo que no es null el miembro a añadir
        if(miembro==null){
            return;
        }
        //Si no es un solar, no tiene grupo (no tiene sentido)
        if(!Casilla.TSOLAR.equals(miembro.getTipo())){
            System.out.println("En este diseño solo tiene sentido agrupar solares");
            return;
        }
        //Se añade únicamente si no está en el array
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
        //Si hay alguna incongruencia, como jugador nulo, 0 casillas en el grupo o un grupo con un numero distinto de casillas que de miembros, devuelve falso
        if (jugador == null || miembros.size() != numCasillas || numCasillas == 0) {
            return false;
        }
        //Recorro todas las casillas
        for (Casilla c : miembros) {
            //Tomo al dueño de la casilla (si es que existe)
            Jugador d = c.getDueno();
            if (d == null || d != jugador) {
                return false;
            }
        }
        //En otro caso sí es dueño
        return true;
    }

    //Añado un getter del color
    public String getColorGrupo(){
        return colorGrupo;
    }

    // endregion

}
