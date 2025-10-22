package partida;


public class Dado {

    // region ==== ATRIBUTOS ====

    private int valor;

    // endregion

    // region ==== MÉTODOS ====

    //Función para simular lanzamiento de un dado: devolverá un valor aleatorio entre 1 y 6.
    public int hacerTirada() {
        this.valor = (int) (Math.random()*6) +1;//sumamos uno porque genera del 0 al 5
        return valor;
    }

    // endregion

}
