package monopoly.consola;

import monopoly.exceptions.BancarrotaException;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;

public interface Comando {
    // --- Comandos Parte 1 (Creación y Movimiento) ---
    void crearJugador(String nombre, String tipoAvatar) throws MonopolyEtseException;
    void lanzarDados() throws MonopolyEtseException;
    void lanzarDadosForzado(int d1, int d2) throws MonopolyEtseException; // Faltaba este (Requisito 7)
    void acabarTurno() throws BancarrotaException;
    void salirCarcel() throws MonopolyEtseException;

    // --- Comandos Parte 1 (Acciones y Consultas) ---
    void comprar(String nombre) throws MonopolyEtseException;
    void verTablero();
    void listarJugadores() throws MonopolyEtseException;
    void listarAvatares() throws MonopolyEtseException;
    void listarVenta() throws MonopolyEtseException;

    // --- Métodos de Descripción (Faltaban estos para que Menu.java funcione bien) ---
    void descJugador(String nombre) throws MonopolyEtseException; // Para "describir jugador X"
    void descAvatar(String id) throws MonopolyEtseException;      // Para "describir avatar X"
    void descCasilla(String nombre) throws MonopolyEtseException; // Para "describir SolarX"

    // --- Comandos Parte 2 (Edificios e Hipotecas) ---
    void edificarCasa() throws MonopolyEtseException;
    void edificarHotel() throws MonopolyEtseException;
    void edificarPiscina() throws MonopolyEtseException;
    void edificarPista() throws MonopolyEtseException;
    void listarEdificios(String color) throws MonopolyEtseException;
    void hipotecar(String nombre) throws MonopolyEtseException;
    void deshipotecar(String nombre) throws MonopolyEtseException;
    void venderPropiedad(String tipo, String solar, int cantidad) throws MonopolyEtseException;

    // --- Comandos Estadísticas ---
    void estadisticasJugador(String nombre);
    void estadisticasJuego();

    // --- Nuevos métodos de la Parte 3 (Tratos) ---
    void proponerTrato(String comando) throws MonopolyEtseException;
    void aceptarTrato(String idTrato)throws MonopolyEtseException;
    void listarTratos();
    void eliminarTrato(String idTrato) throws MonopolyEtseException;
    void declararBancarrota();
}