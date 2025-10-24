import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        long inicio=System.currentTimeMillis();
        Estacionamiento estacionamiento = new Estacionamiento();
        ArrayList<Coche> coches = new ArrayList<>();

        // Crear 15 coches, 1 de cada 3 es VIP
        for (int i = 1; i <= 15; i++) {
            boolean vip = (i % 3 == 0);
            Coche coche = new Coche("Coche " + i, estacionamiento, vip);
            coches.add(coche);
            coche.start();
        }

        // Hilo que muestra el estado cada 1 segundo
        Thread monitor = new Thread(() -> {
            while (estacionamiento.isActivo()) {
                try {
                    Thread.sleep(1000);
                    estacionamiento.mostrarEstadisticas();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        monitor.setDaemon(true);
        monitor.start();

        // Esperar a que terminen todos los coches
        for (Coche c : coches) {
            try {
                c.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Finalizar simulación
        estacionamiento.detener();
        estacionamiento.mostrarEstadisticasFinales();

        System.out.println("Todos los coches han intentado aparcar.");
        long fin=System.currentTimeMillis();
        System.out.println("Tiempo total: "+(fin-inicio)/1000+" segundos");
    }
}
