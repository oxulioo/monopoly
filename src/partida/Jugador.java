package partida;

import monopoly.*;
import java.util.ArrayList;


public class Jugador {
    private String nombre; //Nombre del jugador
    private Avatar avatar; //Avatar que tiene en la partida.
    private float fortuna; //Dinero que posee.
    private float gastos; //Gastos realizados a lo largo del juego.
    private boolean enCarcel; //Será true si el jugador está en la carcel
    private int tiradasCarcel; //Cuando está en la carcel, contará las tiradas sin éxito que ha hecho allí para intentar salir (se usa para limitar el numero de intentos).
    private int vueltas; //Cuenta las vueltas dadas al tablero.
    private ArrayList<Casilla> propiedades; //Propiedades que posee el jugador.

    //Constructor vacío. Se usará para crear la banca.
    public Jugador() {
        this.nombre = "Banca";
        this.avatar = null; // La banca no se representa en el tablero con avatar
        this.fortuna = Float.MAX_VALUE; // Dinero "infinito"
        this.gastos = 0;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }
    public Avatar getAvatar(){
        return avatar;
    }
    public float getFortuna() {
        return fortuna;
    }
    public void setFortuna(float f){
        this.fortuna = f;
    }
    public boolean isEnCarcel() {
        return enCarcel;
    }
    public void entrarEnCarcel()   { enCarcel = true; }
    public void salirCarcel()      { enCarcel = false; }
    public int getVueltas(){
        return vueltas;
    }
    public void setVueltas(int v){
    }

    public ArrayList<Casilla> getPropiedades() {
        return propiedades;
    }

        /*Constructor principal. Requiere parámetros:
         * Nombre del jugador, tipo del avatar que tendrá, casilla en la que empezará y ArrayList de
         * avatares creados (usado para dos propósitos: evitar que dos jugadores tengan el mismo nombre y
         * que dos avatares tengan mismo ID). Desde este constructor también se crea el avatar.
         */

    public Jugador(String nombre, String tipoAvatar, Casilla inicio, ArrayList<Avatar> avCreados) {
        //verificar que el nombre no exista en el array de avatares creados
        if(existeNombre(nombre, avCreados)){
            System.out.println("Jugador existe");
            return; //se debería lanzar una Excepción
        }

        this.nombre = nombre;
        this.fortuna = 15000000; // Todos los jugadores empiezan con 15M
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();

        // Creamos el avatar (pasamos tipo y lista de avatares ya creados para asegurar unicidad)
        this.avatar = new Avatar(tipoAvatar, inicio, this, avCreados);

        // Colocamos el avatar en la casilla inicial (Salida)
        //this.avatar.setPosicion(inicio);

    }

    private boolean existeNombre(String nombre, ArrayList<Avatar> avCreados){
        for (Avatar avatar : avCreados){
            if (avatar.getJugador().getNombre().equals(nombre))
                return true;
        }
        return false;
    }

    //Otros métodos:
    //Método para añadir una propiedad al jugador. Como parámetro, la casilla a añadir.
    public void anhadirPropiedad(Casilla casilla) {
        if(casilla == null){
            System.out.println("La casilla no existe");
            return;
        }
        String tipo = casilla.getTipo();
        if(tipo.equals("Impuesto") || tipo.equals("Suerte") || tipo.equals("Caja") || tipo.equals("IrACarcel") || tipo.equals("Carcel") || tipo.equals("Parking")){
            System.out.println("La casilla no es una propiedad, por lo que no se puede añadir al jugador");
            return;
        }
        if (casilla.getDuenho() != null && casilla.getDuenho() != this){
            System.out.println("La casilla "+ casilla.getNombre() + " pertenece al jugador " + casilla.getDuenho().getNombre()+ ".");
            return;
        }
        if(!propiedades.contains(casilla)){
            propiedades.add(casilla);
            casilla.setDuenho(this);
            System.out.println(nombre + " ha adquirido la propiedad " + casilla.getNombre());
        }else{
            System.out.println("La casilla ya pertenece al jugador");
        }
    }

    //Método para eliminar una propiedad del arraylist de propiedades de jugador.
    public void eliminarPropiedad(Casilla casilla) {
        if(casilla == null){
            System.out.println("La casilla no existe");
            return;
        }
        if(!propiedades.contains(casilla)){
            System.out.println("La casilla no pertenece al jugador");
            return;
        }
        propiedades.remove(casilla);
        casilla.setDuenho(null);
    }

    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public void sumarGastos(float valor) {
        this.fortuna -= valor;
        if (this.fortuna < 0) {
            this.declararBancarrota();
        }
    }

    public void recibirDinero(float valor) {
        this.fortuna += valor;
    }

    public void declararBancarrota(){
        System.out.println(nombre + " ha sido declarado en bancarrota");
        this.propiedades.clear();
    }

    public void comprarPropiedad(Casilla c){
        if(c.getDuenho()!= null){
            System.out.println("La propiedad ya tiene un dueño actualmente");
            return;
        }
        if(this.fortuna>=c.getValor()){
            this.fortuna-=c.getValor();
            c.setDuenho(this);
            this.propiedades.add(c);
        }else{
            System.out.println("No tiene dinero suficiente para comprar la propiedad "+ c.getNombre());
        }
    }



    /*Método para establecer al jugador en la cárcel.
     * Se requiere disponer de las casillas del tablero para ello (por eso se pasan como parámetro).*/
    public void encarcelar(ArrayList<ArrayList<Casilla>> pos) {
        this.enCarcel = true;
        this.tiradasCarcel = 0;
    }

    public void pagarAlquiler(Casilla c){
        Jugador dueno = c.getDuenho();
        if(dueno != null && dueno != this){
            float alquiler = c.getValor();
            this.pagarAlquiler(c);
            dueno.sumarFortuna(alquiler);
            System.out.println(this.nombre + " ha pagado " + alquiler + " € a " + dueno.getNombre());
        }
    }

    public void pagarImpuesto(float valor){
        if(valor <= 0){
            System.out.println("Precio del impuesto no válido");
            return;
        }
        if(this.fortuna >= valor) {
            this.fortuna -= valor;
            this.sumarGastos(valor);
            System.out.println(nombre + " paga un impuesto de " + valor + "€");
            return;
        }else{
            System.out.println(nombre + " no tiene dinero suficiente para pagar el impuesto");
            this.declararBancarrota();
            return;
        }
    }



}

