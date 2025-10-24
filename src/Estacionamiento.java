import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Estacionamiento {
    private final int capacidadMaxima = 5;
    private final Semaphore semaforo = new Semaphore(capacidadMaxima);
    private final ArrayList<Coche> cochesAparcados = new ArrayList<>();

    private int cochesAparcaron = 0;
    private int cochesFuera = 0;
    private int cochesDesalojados = 0;

    private volatile boolean activo = true;

    public boolean entrar(Coche coche) {
        try {
            if (coche.esVip()) {
                // Los VIP esperan 5s; si no consiguen entrar, desalojan a un normal
                if (semaforo.tryAcquire(5, TimeUnit.SECONDS)) {
                    synchronized (this) {
                        cochesAparcados.add(coche);
                        cochesAparcaron++;
                        System.out.println(coche + " ha entrado (VIP).");
                    }
                    return true;
                } else {
                    // Desaloja si no hay sitio
                    synchronized (this) {
                        if (cochesAparcados.size() >= capacidadMaxima) {
                            desalojarCocheNormal(coche);
                        }
                        cochesAparcados.add(coche);
                        cochesAparcaron++;
                        System.out.println(coche + " ha entrado desalojando a un coche normal.");
                    }
                    semaforo.acquire(); // ocupar la plaza liberada
                    return true;
                }
            } else {
                // Coches normales esperan 5s por un permiso
                if (semaforo.tryAcquire(5, TimeUnit.SECONDS)) {
                    synchronized (this) {
                        cochesAparcados.add(coche);
                        cochesAparcaron++;
                        System.out.println(coche + " ha entrado.");
                    }
                    return true;
                } else {
                    synchronized (this) {
                        cochesFuera++;
                        System.out.println(coche + " no pudo entrar (parking lleno).");
                    }
                    return false;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public synchronized void salir(Coche coche) {
        if (cochesAparcados.remove(coche)) {
            semaforo.release();
            System.out.println(coche + " ha salido.");
        }
    }

    private synchronized void desalojarCocheNormal(Coche cocheVip) {
        for (Coche coche : new ArrayList<>(cochesAparcados)) {
            if (!coche.esVip()) {
                cochesAparcados.remove(coche);
                cochesDesalojados++;
                System.out.println(coche + " fue desalojado por " + cocheVip);
                semaforo.release();
                break;
            }
        }
    }

    public void mostrarEstadisticas() {
        synchronized (this) {
            System.out.println("===== ESTADO ESTACIONAMIENTO =====");
            System.out.println("Plazas libres: " + semaforo.availablePermits());
            System.out.println("Coches dentro: " + cochesAparcados);
            System.out.println("==================================");
        }
    }

    public void mostrarEstadisticasFinales() {
        System.out.println("\n===== ESTADÍSTICAS FINALES =====");
        System.out.println("Coches que lograron entrar: " + cochesAparcaron);
        System.out.println("Coches que no lograron entrar: " + cochesFuera);
        System.out.println("Coches desalojados: " + cochesDesalojados);
    }

    public boolean isActivo() {
        return activo;
    }

    public void detener() {
        activo = false;
    }
}
