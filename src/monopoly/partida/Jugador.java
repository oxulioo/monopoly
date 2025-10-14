<<<<<<< HEAD:esqueleto_parte_1/partida/Jugador.java
package partida;

import java.util.ArrayList;

import monopoly.*;


public class Jugador {

    //Atributos:
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
        //verificar que dos jugadores no tengan en el mismo tipo de avatar
        //se logra al crear el nuevo avatar
        Avatar avatar= new avatar (); //pasamos this como jugador

        this.nombre = nombre;
        this.fortuna = 15000000; // Todos los jugadores empiezan con 15M
        this.gastos = 0;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();

        // Creamos el avatar (pasamos tipo y lista de avatares ya creados para asegurar unicidad)
        this.avatar = new Avatar(tipoAvatar, avCreados);

        // Colocamos el avatar en la casilla inicial (Salida)
        //this.avatar.setPosicion(inicio);
    }
    private boolean existeNombre(String nombre, ArrayList<Avatar> avCreadps){
        for (Avatar avatar : avCreados){
            if (avatar.getJugador().getNombre().equals(nombre))
                return true;
        }
    }


    //Otros métodos:
    //Método para añadir una propiedad al jugador. Como parámetro, la casilla a añadir.
    public void anhadirPropiedad(Casilla casilla) {
    }

    //Método para eliminar una propiedad del arraylist de propiedades de jugador.
    public void eliminarPropiedad(Casilla casilla) {
    }

    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public void sumarGastos(float valor) {
        this.gastos += valor;
    }

    /*Método para establecer al jugador en la cárcel. 
    * Se requiere disponer de las casillas del tablero para ello (por eso se pasan como parámetro).*/
    public void encarcelar(ArrayList<ArrayList<Casilla>> pos) {
    }


    //Metodo para obtener el nombre del jugador

    public String getNombre() {
        return nombre;
    }

}
=======
package partida;

import java.util.ArrayList;

import monopoly.*;


public class Jugador {

    //Atributos:
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
        //verificar que dos jugadores no tengan en el mismo tipo de avatar
        //se logra al crear el nuevo avatar
        Avatar avatar= new avatar ();

        this.nombre = nombre;
        this.fortuna = 15000000; // Todos los jugadores empiezan con 15M
        this.gastos = 0;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();

        // Creamos el avatar (pasamos tipo y lista de avatares ya creados para asegurar unicidad)
        this.avatar = new Avatar(tipoAvatar, avCreados);

        // Colocamos el avatar en la casilla inicial (Salida)
        //this.avatar.setPosicion(inicio);
    }
    private boolean existeNombre(String nombre, ArrayList<Avatar> avCreadps){
        for (Avatar avatar : avCreados){
            if (avatar.getJugador().getNombre().equals(nombre))
                return true;
        }
    }


    //Otros métodos:
    //Método para añadir una propiedad al jugador. Como parámetro, la casilla a añadir.
    public void anhadirPropiedad(Casilla casilla) {
    }

    //Método para eliminar una propiedad del arraylist de propiedades de jugador.
    public void eliminarPropiedad(Casilla casilla) {
    }

    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public void sumarGastos(float valor) {
        this.gastos += valor;
    }

    /*Método para establecer al jugador en la cárcel. 
    * Se requiere disponer de las casillas del tablero para ello (por eso se pasan como parámetro).*/
    public void encarcelar(ArrayList<ArrayList<Casilla>> pos) {
        enCarcel = true;
        tiradasCarcel = 0;
        pos = 10;

    }

    //Metodo para obtener el nombre del jugador

    public String getNombre() {
        return nombre;
    }

}
>>>>>>> 8e96b88b4123c02115ce9200fe46bbe14a44b662:src/monopoly/partida/Jugador.java
