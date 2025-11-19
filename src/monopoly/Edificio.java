package monopoly;
import partida.Jugador;

public final class Edificio {
    public enum Tipo { CASA, HOTEL, PISCINA, PISTA }

    private final long id;
    private final Tipo tipo;
    private final Casilla solar;
    private final Jugador propietario;


    public Edificio(long id, Tipo tipo, Casilla solar, Jugador propietario) {
        this.id = id; this.tipo = tipo; this.solar = solar; this.propietario = propietario;
    }
    public long getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public Casilla getSolar() { return solar; }
    public Jugador getPropietario() { return propietario; }


    public void eliminar() {
        Casilla s = getSolar();
        Jugador p = getPropietario();
        if (s == null) return;

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
    }




}

