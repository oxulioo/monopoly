package monopoly;

import monopoly.exceptions.MonopolyEtseException;

public interface Comando {
    // --- Comandos Parte 1 (Creación y Movimiento) ---
    void crearJugador(String nombre, String tipoAvatar) throws MonopolyEtseException;
    void lanzarDados() throws MonopolyEtseException;
    void lanzarDadosForzado(int d1, int d2); // Faltaba este (Requisito 7)
    void acabarTurno();
    void salirCarcel();

    // --- Comandos Parte 1 (Acciones y Consultas) ---
    void comprar(String nombre);
    void verTablero();
    void listarJugadores();
    void listarAvatares();
    void listarVenta();

    // --- Métodos de Descripción (Faltaban estos para que Menu.java funcione bien) ---
    void descJugador(String nombre) throws MonopolyEtseException; // Para "describir jugador X"
    void descAvatar(String id) throws MonopolyEtseException;      // Para "describir avatar X"
    void descCasilla(String nombre) throws MonopolyEtseException; // Para "describir SolarX"

    // --- Comandos Parte 2 (Edificios e Hipotecas) ---
    void edificarCasa();
    void edificarHotel();
    void edificarPiscina();
    void edificarPista();
    void listarEdificios(String color);
    void hipotecar(String nombre);
    void deshipotecar(String nombre);
    void venderPropiedad(String tipo, String solar, int cantidad);

    // --- Comandos Estadísticas ---
    void estadisticasJugador(String nombre);
    void estadisticasJuego();

    // --- Nuevos métodos de la Parte 3 (Tratos) ---
    void proponerTrato(String comando);
    void aceptarTrato(String idTrato);
    void listarTratos();
    void eliminarTrato(String idTrato);
}