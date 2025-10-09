package partida;


public class Dado {
    //El dado solo tiene un atributo en nuestro caso: su valor.
    private int valor;

    //Metodo para simular lanzamiento de un dado: devolverá un valor aleatorio entre 1 y 6.
    public int hacerTirada() {
        this.valor = (int) (Math.random()*6) +1;
        return valor;
    }
    //Lanzamos 2 dados y sumamos sus valores.
    public int lanzarDados(){
        private int dado1 = hacerTirada();
        private int dado2 = hacerTirada();
        public boolean esdoble(){ return dado1 == dado2;
        return dado1+dado2;
    }
}
