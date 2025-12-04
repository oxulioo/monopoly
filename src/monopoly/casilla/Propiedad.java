package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;

public abstract class Propiedad extends Casilla {

    // MOVIDO TAL CUAL DESDE CASILLA.JAVA
    protected Jugador dueno;
    protected int valor;
    protected Grupo grupo;
    protected int hipoteca;
    protected int hipotecada; // Mantengo tu int hipotecada (0 o 1) en vez de boolean
    protected long dineroGenerado=0;
    public long getDineroGenerado(){
        return dineroGenerado;
    }
    public void sumarDineroGenerado(long cantidad){
        this.dineroGenerado+=cantidad;
    }
    public Propiedad(String nombre, String tipo, int posicion, int valor, int hipoteca, Jugador dueno) {
        super(nombre, tipo, posicion); // ¡Ahora sí le pasamos el tipo a Casilla!
        this.valor = valor;
        this.hipoteca = hipoteca;
        this.dueno = dueno;
        this.hipotecada = 0;
        this.grupo = null;
    }

    // --- MÉTODOS MOVIDOS DE CASILLA.JAVA ---

    public Jugador getDueno() {
        return dueno;
    }

    public void setDueno(Jugador d) {
        this.dueno = d;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo g) {
        this.grupo = g;
    }

    public int getValor() {
        return valor;
    }

    public int getHipoteca() {
        return hipoteca;
    }

    public void setHipoteca(int hip) {
        this.hipoteca = hip;
    }

    public int gethipotecada() {
        return hipotecada;
    }

    public void sethipotecada(int h) {
        hipotecada = h;
    }

    // Requisito 26: boolean perteneceAJugador(Jugador jugador)
    // Implementación simple para cumplir
    public boolean perteneceAJugador(Jugador jugador) {
        return dueno != null && dueno.equals(jugador);
    }

    // Requisito 26: abstract boolean alquiler() y abstract float valor()
    // Los definimos abstractos para que Solar los implemente con TU lógica
    public abstract boolean alquiler(Jugador j);

    public abstract int valor();

    // MOVIDO: Tu método comprarCasilla
    // Lógica idéntica a tu Casilla.java
    public void comprar(Jugador solicitante) {
        int precio = Math.max(0, this.valor);

        if (!solicitante.sumarGastos(precio)) {
            System.out.println(solicitante.getNombre() + " no tiene suficiente dinero para comprar " + this.nombre);
            return;
        }
        // Se añade la propiedad al solicitante.
        solicitante.anadirPropiedad(this);
    }

    // Implementación base de evaluarCasilla para Propiedades
    // Usa TU lógica original de Casilla.java para cuando no tiene dueño
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita(); // Tu línea original

        // TU LÓGICA: Si tiene dueño y no soy yo -> Pagar
        if (dueno != null && !dueno.equals(actual) && !dueno.getNombre().equals("Banca")) {
            // Llama al método alquiler() específico de cada hijo (Solar/Transporte)
            alquiler(actual);
        }
        // TU LÓGICA: Si no tiene dueño -> Info
        else if (dueno == null || dueno.getNombre().equals("Banca")) {
            Juego.consola.imprimir("Estás en " + nombre + ". Pertenece a la Banca.");
            Juego.consola.imprimir("Valor de compra: " + valor);
        }
    }
}