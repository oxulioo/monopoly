package partida;

import monopoly.*;
import java.util.ArrayList;

public class Jugador {

    private String nombre;
    private Avatar avatar;
    private int fortuna;
    private boolean enCarcel;
    private int tiradasCarcel;
    private int vueltas;
    private ArrayList<Casilla> propiedades;
    private final java.util.List<Edificio> misEdificios = new java.util.ArrayList<>();
    private EstadisticasJugador estadisticas;

    public java.util.List<Edificio> getMisEdificios() { return java.util.Collections.unmodifiableList(misEdificios); }
    public void anadirEdificio(Edificio e) { misEdificios.add(e); }
    public void eliminarEdificio(Edificio e) { misEdificios.remove(e); }
    public String getNombre() { return nombre; }
    public Avatar getAvatar() { return avatar; }
    public int getFortuna() { return fortuna; }
    public boolean isEnCarcel() { return enCarcel; }
    public void salirCarcel() { enCarcel = false; }
    public int getVueltas() { return vueltas; }
    public void setVueltas(int v) { this.vueltas = v; }

    public ArrayList<Casilla> getPropiedades() {
        if (propiedades == null) propiedades = new ArrayList<>();
        return propiedades;
    }

    public int getTiradasCarcel() { return tiradasCarcel; }
    public void setTiradasCarcel(int t) { this.tiradasCarcel = t; }
    public EstadisticasJugador getEstadisticas() { return estadisticas; }

    public Jugador() {
        this.nombre = "Banca";
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.fortuna = Valor.FORTUNA_BANCA;
        this.propiedades = new ArrayList<>();
        this.avatar = new Avatar("Banca", null, this, new ArrayList<>());
        this.estadisticas = new EstadisticasJugador();
    }

    public Jugador(String nombre, String tipoAvatar, Casilla casilla, ArrayList<Avatar> avCreados) {
        if (existeNombre(nombre, avCreados)) {
            Juego.consola.imprimir("Jugador existe");
            return;
        }
        this.nombre = nombre;
        this.fortuna = Valor.FORTUNA_INICIAL;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();
        this.avatar = new Avatar(tipoAvatar, casilla, this, avCreados);
        this.avatar.setPosicion(casilla);
        this.estadisticas = new EstadisticasJugador();
    }

    private boolean existeNombre(String nombre, ArrayList<Avatar> avCreados) {
        for (Avatar avatar : avCreados) {
            if (avatar.getJugador().getNombre().equals(nombre)) return true;
        }
        return false;
    }

    public void anadirPropiedad(Casilla casilla) {
        if (casilla == null) {
            Juego.consola.imprimir("Error: La casilla no existe");
            return;
        }

        // CORRECCIÓN: Comprobamos si es Propiedad
        if (!(casilla instanceof Propiedad)) {
            Juego.consola.imprimir("La casilla " + casilla.getNombre() + " no es una propiedad.");
            return;
        }

        Propiedad p = (Propiedad) casilla;

        if (p.getDueno() != null && p.getDueno() != this && !"Banca".equals(p.getDueno().getNombre())) {
            Juego.consola.imprimir("La casilla " + p.getNombre() + " pertenece al jugador " + p.getDueno().getNombre() + ".");
            return;
        }

        if (!propiedades.contains(p)) {
            propiedades.add(p);
            p.setDueno(this);
        } else {
            Juego.consola.imprimir("La casilla " + p.getNombre() + " ya pertenece al jugador");
        }
    }

    public void sumarFortuna(int valor) { this.fortuna += valor; }

    public boolean sumarGastos(int valor) {
        if (this.fortuna < valor) {
            Juego.consola.imprimir("No tienes suficiente dinero.");
            return false;
        }
        this.fortuna -= valor;
        return true;
    }

    public void encarcelar() {
        this.enCarcel = true;
        this.tiradasCarcel = 0;
        this.estadisticas.incrementarVecesEnLaCarcel();
    }

    public void pagarAlquiler(Casilla c, int factor_pago) {
        if (c == null) return;

        // CORRECCIÓN: Verificamos que sea Propiedad
        if (!(c instanceof Propiedad)) return;
        Propiedad p = (Propiedad) c;

        Jugador dueno = p.getDueno();
        if (dueno != null && dueno != this) {
            int alquiler = 0;

            // Si es solar, calculamos con edificios
            if (c instanceof Solar) {
                Solar s = (Solar) c;
                int casas = s.getNumCasas();
                int hoteles = s.getNumHoteles();
                int piscinas = s.getNumPiscinas();
                int pistas = s.getNumPistas();

                if (casas > 0 || hoteles > 0 || piscinas > 0 || pistas > 0) {
                    if (casas > 0) alquiler += casas * s.getAlquilerCasa();
                    if (hoteles > 0) alquiler += hoteles * s.getAlquilerHotel();
                    if (piscinas > 0) alquiler += piscinas * s.getAlquilerPiscina();
                    if (pistas > 0) alquiler += pistas * s.getAlquilerPistaDeporte();
                } else {
                    alquiler = s.getAlquilerBase();
                }
            } else {
                // Transporte o Servicio (casillas que son propiedad pero no solar)
                // Usamos una lógica genérica o accedemos a los valores definidos
                if (c instanceof Transporte) alquiler = Valor.ALQUILER_TRANSPORTE;
                else if (c instanceof Servicio) alquiler = Valor.FACTOR_SERVICIO; // Ojo, servicio depende de dados, esto es simplificado
            }

            int importe = factor_pago * alquiler;

            boolean ok = this.sumarGastos(importe);
            if (ok) {
                this.estadisticas.sumarPagoDeAlquileres(importe);
                dueno.sumarFortuna(importe);
                dueno.getEstadisticas().sumarCobroDeAlquileres(importe);

                // Si es Solar, tiene metodo sumarDineroGenerado propio, o lo casteamos
                if (c instanceof Solar) {
                    ((Solar)c).sumarDineroGenerado(importe);
                    if (p.getGrupo() != null) {
                        p.getGrupo().sumarRentabilidad(importe);
                    }
                }
                Juego.consola.imprimir(this.nombre + " ha pagado " + importe + " € a " + dueno.getNombre());
            } else {
                Juego.consola.imprimir(this.nombre + " no puede pagar el alquiler de " + importe + " €.");
            }
        }
    }
}