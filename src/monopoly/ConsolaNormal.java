package monopoly;

import java.util.Scanner;

public class ConsolaNormal implements Consola {
    private final Scanner sc = new Scanner(System.in);

    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public String leer(String descripcion) {
        if (descripcion != null) {
            System.out.print(descripcion);
        }
        return sc.nextLine();
    }
}