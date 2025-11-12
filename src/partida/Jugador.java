package partida;

import monopoly.*;
import java.util.ArrayList;


public class Jugador {

    private String nombre; //Nombre del jugador
    private Avatar avatar; //Avatar que tiene en la partida.
    private int fortuna; //Dinero que posee.
    private boolean enCarcel; //Será true si el jugador está en la carcel
    private int tiradasCarcel; //Cuando está en la carcel, contará las tiradas sin éxito que ha hecho allí para intentar salir (se usa para limitar el número de intentos).
    private int vueltas; //Cuenta las vueltas dadas al tablero.
    private ArrayList<Casilla> propiedades; //Propiedades que posee el jugador.
    private final java.util.List<Edificio> misEdificios = new java.util.ArrayList<>();



    public java.util.List<Edificio> getMisEdificios() { return java.util.Collections.unmodifiableList(misEdificios); }
    public void anadirEdificio(Edificio e) { misEdificios.add(e); }
    public void eliminarEdificio(Edificio e){ misEdificios.remove(e); }
    public String getNombre() {return nombre;}
    public Avatar getAvatar() {return avatar;}
    public int getFortuna() {return fortuna;}
    public boolean isEnCarcel() {return enCarcel;}
    public void salirCarcel() {enCarcel = false;}
    public int getVueltas() {return vueltas;}
    public void setVueltas(int v) {this.vueltas=v;}
    public ArrayList<Casilla> getPropiedades() {
        if (propiedades==null) propiedades=new ArrayList<>();
        return propiedades;
    }
    public int getTiradasCarcel() {return tiradasCarcel;}
    public void setTiradasCarcel(int t) {this.tiradasCarcel=t;}


    /*Constructor principal. Requiere parámetros:
     * Nombre del jugador, tipo del avatar que tendrá, casilla en la que empezará y ArrayList de
     * avatares creados (usado para dos propósitos: evitar que dos jugadores tengan el mismo nombre y
     * que dos avatares tengan mismo ID). Desde este constructor también se crea el avatar.
     */

    public Jugador(String nombre, String tipoAvatar, Casilla casilla, ArrayList<Avatar> avCreados) {
        //verificar que el nombre no exista en el array de avatares creados
        if (existeNombre(nombre, avCreados)) {
            System.out.println("Jugador existe");
            return;
        }

        this.nombre = nombre;
        this.fortuna = 15000000; // Todos los jugadores empiezan con 15M
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();

        // Creamos el avatar (pasamos tipo y lista de avatares ya creados para asegurar unicidad)
        this.avatar = new Avatar(tipoAvatar, casilla, this, avCreados);

        // Colocamos el avatar en la casilla inicial (Salida)
        this.avatar.setPosicion(casilla);

    }

    //Funcion que verifica si ya existe un nombre en el array de avatares creados.
    private boolean existeNombre(String nombre, ArrayList<Avatar> avCreados) {
        for (Avatar avatar : avCreados) {
            if (avatar.getJugador().getNombre().equals(nombre))
                return true;
        }
        return false;
    }

    public void anadirPropiedad(Casilla casilla) {
        if (casilla == null) {
            System.out.println("La casilla no existe");
            return;
        }
        String tipo = casilla.getTipo();
        //Si el tipo de casilla no se puede comprar
        if (tipo.equals("Impuesto") || tipo.equals("Suerte") || tipo.equals("Caja") || tipo.equals("IrACarcel") || tipo.equals("Carcel") || tipo.equals("Parking")) {
            System.out.println("La casilla no es una propiedad, por lo que no se puede añadir al jugador");
            return;
        }
        //La casillla ya pertenece a un jugador
        if (casilla.getDueno() != null && casilla.getDueno() != this) {
            System.out.println("La casilla " + casilla.getNombre() + " pertenece al jugador " + casilla.getDueno().getNombre() + ".");
            return;
        }
        //Si no pertenece a nadie se la añadimos, sino que solo queda que sea suya
        if (!propiedades.contains(casilla)) {
            propiedades.add(casilla);
            casilla.setDueno(this);
            System.out.println(nombre + " ha adquirido la propiedad " + casilla.getNombre());
        } else {
            System.out.println("La casilla ya pertenece al jugador");
        }
    }


    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(int valor) {
        this.fortuna += valor;
    }

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public boolean sumarGastos(int valor) {
        this.fortuna -= valor; //a su fortuna le restamos el valor
        if (this.fortuna < 0) {
            this.declararBancarrota(); //si es negativa se declara en Bancarrota
            return false;
        }
        return true;
    }

    public void declararBancarrota() {
        System.out.println(nombre + " ha sido declarado en bancarrota");
        this.propiedades.clear();//elimina todas sus propiedades
    }

    public void comprarPropiedad(Casilla c) {
        if (c.getDueno() != null ) {
            System.out.println("La propiedad ya tiene un dueño actualmente");
            return;
        }
        if (this.fortuna >= c.getValor()) { //si su fortuna es mayor o igual que el valor de la casilla, la compra
            this.fortuna -= c.getValor();

            c.setDueno(this);
            this.propiedades.add(c);

        } else {
            System.out.println("No tiene dinero suficiente para comprar la propiedad " + c.getNombre());
        }
    }

    /*Método para establecer al jugador en la cárcel.
     * Se requiere disponer de las casillas del tablero para ello (por eso se pasan como parámetro).*/
    public void encarcelar() {
        this.enCarcel = true;
        this.tiradasCarcel = 0;
    }

    public void pagarAlquiler(Casilla c, int factor_pago) {
        if (c == null) return;
        Jugador dueno = c.getDueno();
        if (dueno != null && dueno != this) {
            int alquiler = c.getAlquiler();        // lo correcto es el alquiler
            boolean ok = this.sumarGastos(alquiler); // resta solo una vez
            if (ok) {
                dueno.sumarFortuna(factor_pago*alquiler);
                System.out.println(this.nombre + " ha pagado " + alquiler*factor_pago + " € a " + dueno.getNombre());
            } else {
                System.out.println(this.nombre + " no puede pagar el alquiler de " + alquiler*factor_pago + " €.");
            }
        }
    }
    public void pagarImpuesto(int valor) {
        if (valor <= 0) {
            System.out.println("Precio del impuesto no válido");
            return;
        }
        boolean ok = this.sumarGastos(valor); //  restamos
        if (ok) {
            System.out.println(nombre + " paga un impuesto de " + (long)valor + "€");
        } else {
            System.out.println(nombre + " no tiene dinero suficiente para pagar el impuesto");
        }
    }


}
