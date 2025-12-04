package monopoly.consola;

import monopoly.Juego;

import java.util.Scanner;

public class ConsolaNormal implements Consola {
    private final Scanner sc = new Scanner(System.in);

    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public void imprimirSinSalto(String mensaje){
        System.out.print(mensaje);
    }

    @Override
    public String leer(String descripcion) {
        if (descripcion != null) {
            Juego.consola.imprimirSinSalto(descripcion);
        }
        return sc.nextLine();
    }
}