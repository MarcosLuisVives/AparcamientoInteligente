import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Estacionamiento estacionamiento = new Estacionamiento();
        ArrayList<Coche> coches = new ArrayList<>();

        // Crear 15 coches, 1 de cada 3 es VIP
        for (int i = 0; i < 15; i++) {
            Coche coche = new Coche("Coche " + i, estacionamiento, i % 3 == 0);
            coche.start();
            coches.add(coche);
        }

        // Hilo de estadísticas
        Thread hilo = new Thread(() -> {
            while (estacionamiento.isActivo()) {
                try {
                    Thread.sleep(1000); // cada 1 segundos
                    estacionamiento.mostrarEstadisticas();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Hilo de estadísticas finalizado.");
        });
        hilo.start();

        // Esperar a que terminen todos los coches
        for (Coche c : coches) {
            try {
                c.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Apagar hilo de estadísticas
        estacionamiento.detener();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        estacionamiento.mostrarEstadisticasFinales();
    }
}