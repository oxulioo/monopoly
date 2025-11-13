package monopoly;
import partida.Jugador;

public final class Edificio {
    public enum Tipo { CASA, HOTEL, PISCINA, PISTA }

    private final long id;
    private final Tipo tipo;
    private final Casilla solar;
    private final Jugador propietario;
//    private final int turnoConstruccion;

    public Edificio(long id, Tipo tipo, Casilla solar, Jugador propietario, int turno) {
        this.id = id; this.tipo = tipo; this.solar = solar; this.propietario = propietario; // this.turnoConstruccion = turno;
    }
    public long getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public Casilla getSolar() { return solar; }
    public Jugador getPropietario() { return propietario; }
    // public int getTurnoConstruccion() { return turnoConstruccion; }

    public boolean eliminar() {
        Casilla s = getSolar();
        Jugador p = getPropietario();
        if (s == null) return false;

        // 1) quitar de las listas
        try { s.eliminarEdificio(this); } catch (Throwable ignored) {}
        if (p != null) {
            try { p.eliminarEdificio(this); } catch (Throwable ignored) {}
        }

        // 2) actualizar contadores del solar
        switch (getTipo()) {
            case CASA:
                if (s.getNumCasas() > 0) s.setNumCasas(s.getNumCasas() - 1);
                break;
            case HOTEL:
                if (s.getNumHoteles() > 0) s.setNumHoteles(s.getNumHoteles() - 1);
                break;
            case PISCINA:
                if (s.getNumPiscinas() > 0) s.setNumPiscinas(s.getNumPiscinas() - 1);
                break;
            case PISTA:
                if (s.getNumPistas() > 0) s.setNumPistas(s.getNumPistas() - 1);
                break;
        }
        return true;
    }

    public static boolean eliminarUnoPorTipo(Casilla solar, Jugador propietario, Tipo tipo) {
        if (solar == null || solar.getEdificios() == null) return false;

        Edificio target = null;
        for (Edificio e : solar.getEdificios()) {
            if (e != null && e.getTipo() == tipo && e.getSolar() == solar &&
                    (propietario == null || e.getPropietario() == propietario)) {
                target = e; break;
            }
        }
        return target != null && target.eliminar();
    }




}

