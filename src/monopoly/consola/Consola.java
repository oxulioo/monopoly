package monopoly.consola;

public interface Consola {
    void imprimir(String mensaje);
    void imprimirSinSalto(String mensaje);
    String leer(String descripcion);
}