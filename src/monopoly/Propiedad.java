package monopoly;

import partida.Jugador;

public abstract class Propiedad extends Casilla {

    // MOVIDO TAL CUAL DESDE CASILLA.JAVA
    protected Jugador dueno;
    protected int valor;
    protected Grupo grupo;
    protected int hipoteca;
    protected int hipotecada; // Mantengo tu int hipotecada (0 o 1) en vez de boolean

    public Propiedad(String nombre, int posicion, int valor, int hipoteca, Jugador dueno) {
        super(nombre, posicion);
        this.valor = valor;
        this.hipoteca = hipoteca;
        this.dueno = dueno;
        this.hipotecada = 0; // Inicializamos a 0 como tenías
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
    public abstract float valor(); // El PDF pide float, aunque tú usas int. Lo convertiremos al devolver.

    // MOVIDO: Tu método comprarCasilla (renombrado a comprar por el PDF)
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
    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        // Si tiene dueño y no soy yo y no es la banca -> Alquiler
        if (dueno != null && !dueno.equals(actual) && !dueno.getNombre().equals("Banca")) {
            // Llamamos al método abstracto alquiler que implementará Solar/Transporte con tu código
            alquiler(actual);
        }
        // Si no tiene dueño (o es banca) -> Info de compra (como tenías en Casilla, aunque allí no imprimía nada explícito salvo en debug, aquí lo dejamos listo para comprar)
        else if (dueno == null || dueno.getNombre().equals("Banca")) {
            // Tu código original no hacía print aquí, solo esperaba al comando "comprar".
            // Lo dejamos vacío para respetar tu flujo, o pones un sysout si quieres.
        }
    }
}