package monopoly;

public interface Comando {
    void crearJugador(String nombre, String tipoAvatar);
    void lanzarDados();
    void acabarTurno();
    void salirCarcel();
    void comprar(String nombre);
    void edificarCasa();
    void edificarHotel();
    void edificarPiscina();
    void edificarPista();
    void hipotecar(String nombre);
    void deshipotecar(String nombre);
    void venderPropiedad(String tipo, String solar, int cantidad);
    void estadisticasJugador(String nombre);
    void estadisticasJuego();
    void listarJugadores();
    void listarAvatares();
    void listarVenta();
    void listarEdificios(String color);
    void verTablero();

    // Nuevos métodos de la Parte 3
    void proponerTrato(String comando);
    void aceptarTrato(String idTrato);
    void listarTratos();
    void eliminarTrato(String idTrato);
}