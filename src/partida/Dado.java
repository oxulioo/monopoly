package partida;


public class Dado {

    // region ==== ATRIBUTOS ====

    private int valor;

    // endregion

    // region ==== MÉTODOS ====

    //Función para simular lanzamiento de un dado: devolverá un valor aleatorio entre 1 y 6.
    public int hacerTirada() {
        this.valor = (int) (Math.random()*6) +1;
        return valor;
    }
    //Lanzamos 2 dados y sumamos sus valores.
    public int lanzarDados(){
        int dado1 = hacerTirada();
        int dado2 = hacerTirada();
        return dado1+dado2;
    }

    // endregion

}
