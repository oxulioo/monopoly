package monopoly;

import partida.Jugador;
import java.util.ArrayList;
import java.util.List;

public class Solar extends Propiedad {


    // MOVIDO TAL CUAL DESDE CASILLA.JAVA (Atributos de edificios)
    private int numCasas=0;
    private int numHoteles=0;
    private int numPiscinas=0;
    private int numPistas=0;

    private int precioCasa;
    private int precioHotel;
    private int precioPiscina;
    private int precioPistaDeporte;

    // Alquileres de edificios
    private int alquilerCasa;
    private int alquilerHotel;
    private int alquilerPiscina;
    private int alquilerPistaDeporte;

    // Alquiler base (necesario recuperarlo del precargarDatosCasillas o pasarlo en constructor)
    private int alquilerBase;

    // Lista de edificios (tal cual la tenías)
    private final java.util.List<Edificio> edificios = new java.util.ArrayList<>();

    public Solar(String nombre, int posicion, int valor, int hipoteca, Jugador dueno,
                 int precioCasa, int precioHotel, int precioPiscina, int precioPistaDeporte,
                 int alquilerBase) {

        super(nombre, Casilla.TSOLAR, posicion, valor, hipoteca, dueno);
        this.precioCasa = precioCasa;
        this.precioHotel = precioHotel;
        this.precioPiscina = precioPiscina;
        this.precioPistaDeporte = precioPistaDeporte;
        this.alquilerBase = alquilerBase;
    }

    // --- GETTERS Y SETTERS MOVIDOS DE CASILLA.JAVA ---

    public int getNumCasas() { return Math.max(0,numCasas); }
    public int getNumHoteles() { return numHoteles; }
    public int getNumPiscinas() { return numPiscinas; }
    public int getNumPistas() { return numPistas; }

    public void setNumCasas(int n) { this.numCasas = n; }
    public void setNumHoteles(int n) { this.numHoteles = n; }
    public void setNumPiscinas(int n) { this.numPiscinas = n; }
    public void setNumPistas(int n) { this.numPistas = n; }

    public int getPrecioCasa() { return precioCasa; }
    public int getPrecioHotel() { return precioHotel; }
    public int getPrecioPiscina() { return precioPiscina; }
    public int getPrecioPistaDeporte() { return precioPistaDeporte; }

    public void setAlquilerCasa(int alquiler) { this.alquilerCasa = alquiler; }
    public void setAlquilerHotel(int alquiler) { this.alquilerHotel = alquiler; }
    public void setAlquilerPiscina(int alquiler) { this.alquilerPiscina = alquiler; }
    public void setAlquilerPistaDeporte(int alquiler) { this.alquilerPistaDeporte = alquiler; }

    public int getAlquilerCasa() { return alquilerCasa; }
    public int getAlquilerHotel() { return alquilerHotel; }
    public int getAlquilerPiscina() { return alquilerPiscina; }
    public int getAlquilerPistaDeporte() { return alquilerPistaDeporte; }

    public void setAlquilerBase(int a) { this.alquilerBase = a; }
    public int getAlquilerBase() { return this.alquilerBase; }

    public java.util.List<Edificio> getEdificios() { return java.util.Collections.unmodifiableList(edificios); }
    public void anadirEdificio(Edificio e) { edificios.add(e); }
    public void eliminarEdificio(Edificio e){ edificios.remove(e); }

    // --- IMPLEMENTACIÓN DE MÉTODOS (Lógica extraída de Casilla.java) ---

    @Override
    public float valor() {
        return (float)this.valor;
    }

    @Override
    public boolean alquiler(Jugador actual) {
        // TU LÓGICA DE EVALUAR CASILLA (CASO TSOLAR) PEGADA AQUÍ
        // Recuperamos la lógica de cobro:

        if(this.gethipotecada() == 0) {
            boolean hayEdificios = (this.numCasas > 0 || this.numHoteles > 0 || this.numPiscinas > 0 || this.numPistas > 0);

            if (hayEdificios) {
                // Si hay edificios, pagas normal (1x)
                actual.pagarAlquiler(this, 1);
            } else {
                // Si no hay edificios, miramos si tiene grupo
                if (this.grupo != null && this.grupo.esDuenoGrupo(this.dueno)) {
                    // Si tiene grupo y no hay edificios -> Paga doble (2x)
                    actual.pagarAlquiler(this, 2);
                } else {
                    // Normal
                    actual.pagarAlquiler(this, 1);
                }
            }
            return true;
        }
        return false;
    }


    // Requisito: hipotecar()
    // TU lógica de hipotecar estaba en Juego.hipotecar.
    // El PDF pide que Solar tenga hipotecar().
    public void hipotecar() {
        // Lógica movida si quieres, o dejamos que Juego gestione.
        // Para cumplir expediente PDF:
        this.sethipotecada(1);
    }

    public boolean estaHipotecada() {
        return this.gethipotecada() == 1;
    }

    @Override
    public String toString() {
        return infoCasilla(); // Reutiliza tu infoCasilla si la traes, o reimplementamos el string
    }
/*
    // Método auxiliar para usar tu infoCasilla original
    public String infoCasilla() {
        // COPIA PEGA DE TU INFO CASILLA (parte Solar)
        String grupoStr = (this.grupo != null) ? this.grupo.getColorGrupo() : "-";
        String propietario = (this.dueno == null) ? "Banca" : this.dueno.getNombre();

        return "{\n"
                + "nombre: " + nombre + ",\n"
                + "tipo: solar,\n"
                + "grupo: " + grupoStr + ",\n"
                + "propietario: " + propietario + ",\n"
                + "valor: " + this.valor + ",\n"
                + "alquiler: " + this.alquilerBase + ",\n" // Ojo: alquilerBase
                + "valor hotel: " + precioHotel + ",\n"
                + "valor casa: " + precioCasa + ",\n"
                + "valor piscina: " + precioPiscina + ",\n"
                + "valor pista de deporte: " + precioPistaDeporte + ",\n"
                + "alquiler casa: " + alquilerCasa + ",\n"
                + "alquiler hotel: " + alquilerHotel + ",\n"
                + "alquiler piscina: " + alquilerPiscina + ",\n"
                + "alquiler pista de deporte: " + alquilerPistaDeporte + ",\n"
                + "}";
    }
*/
    // ... (Tu código termina aquí)

    // Método auxiliar para usar tu infoCasilla original
    public String infoCasilla() {
        // COPIA PEGA DE TU INFO CASILLA (parte Solar)
        String grupoStr = (this.grupo != null) ? this.grupo.getColorGrupo() : "-";
        String propietario = (this.dueno == null) ? "Banca" : this.dueno.getNombre();

        String estadoHipoteca = this.gethipotecada() == 1 ? "Sí" : "No";

        // Se han añadido las variables de contador de edificios y el estado de hipoteca al JSON
        return "{\n"
                + "nombre: " + nombre + ",\n"
                + "tipo: solar,\n"
                + "grupo: " + grupoStr + ",\n"
                + "propietario: " + propietario + ",\n"
                + "hipotecada: " + estadoHipoteca + ",\n"
                + "valor: " + this.valor + ",\n"
                + "alquiler: " + this.alquilerBase + ",\n"
                + "num_casas: " + this.getNumCasas() + ",\n"
                + "num_hoteles: " + this.getNumHoteles() + ",\n"
                + "num_piscinas: " + this.getNumPiscinas() + ",\n"
                + "num_pistas: " + this.getNumPistas() + ",\n"
                + "valor hotel: " + precioHotel + ",\n"
                + "valor casa: " + precioCasa + ",\n"
                + "valor piscina: " + precioPiscina + ",\n"
                + "valor pista de deporte: " + precioPistaDeporte + ",\n"
                + "alquiler casa: " + alquilerCasa + ",\n"
                + "alquiler hotel: " + alquilerHotel + ",\n"
                + "alquiler piscina: " + alquilerPiscina + ",\n"
                + "alquiler pista de deporte: " + alquilerPistaDeporte + "\n"
                + "}";
    }

    // --- IMPLEMENTACIÓN ADICIONAL PARA COMPLETAR EL REQUISITO DEL PDF (Solo al final) ---

    // Complemento a hipotecar/estaHipotecada. Necesario para el flujo del juego.
    public void deshipotecar() {
        // La lógica de pago (hipoteca + interés) se debe manejar en Juego.java o Jugador.java.
        // Esta implementación solo cambia el estado de la propiedad.
        this.sethipotecada(0);
    }

    // Requisito 26: void edificar(String tipoEdificio)
    public void edificar(String tipo) {
        // NOTA: La lógica de control (comprobación de reglas, cobro, asignación de ID
        // y la creación del objeto Edificio) se debe ejecutar en Juego.java.
        // Este método asume que el objeto Edificio ya fue creado y añadido a la lista 'edificios'.

        // Aquí solo actualizamos los contadores internos de Solar:
        switch (tipo.toLowerCase()) {
            case "casa":
                this.setNumCasas(this.getNumCasas() + 1);
                break;
            case "hotel":
                // Al construir hotel, se venden las casas (si hay).
                this.setNumHoteles(this.getNumHoteles() + 1);
                this.setNumCasas(0);
                break;
            case "piscina":
                this.setNumPiscinas(this.getNumPiscinas() + 1);
                break;
            case "pista deporte":
                this.setNumPistas(this.getNumPistas() + 1);
                break;
            default:
                Juego.consola.imprimir("Error: Tipo de edificio '" + tipo + "' no reconocido.");
        }
    }

}


