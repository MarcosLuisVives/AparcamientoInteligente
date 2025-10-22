import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Estacionamiento {
    private final int capacidadMaxima = 5;
    private final Semaphore semaforo = new Semaphore(capacidadMaxima);
    private final ArrayList<Coche> coches = new ArrayList<>();

    private final AtomicInteger cochesAparcaron = new AtomicInteger(0);
    private final AtomicInteger cochesFuera = new AtomicInteger(0);
    private final AtomicInteger cochesDesalojados = new AtomicInteger(0);

    private volatile boolean activo = true;

    public synchronized boolean entrar(Coche coche) {
        try {
            if (semaforo.tryAcquire(5, TimeUnit.SECONDS)) { // normales esperan máx 3s
                coches.add(coche);
                System.out.println(coche + " ha entrado");
                cochesAparcaron.incrementAndGet();
                return true;
            } else {
                if (coche.esVip()) {
                    desalojarCocheNormal(coche);
                    semaforo.acquireUninterruptibly(); // ocupa la plaza liberada
                    coches.add(coche);
                    System.out.println(coche + " ha entrado desalojando a un coche normal.");
                    cochesAparcaron.incrementAndGet();
                    return true;
                } else {
                    System.out.println(coche + " no pudo entrar (parking lleno).");
                    cochesFuera.incrementAndGet();
                    return false;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public synchronized void salir(Coche coche) {
        if (coches.remove(coche)) {
            semaforo.release();
            System.out.println(coche + " ha salido");
        }
    }

    private synchronized void desalojarCocheNormal(Coche cocheVip) {
        for (Coche coche : new ArrayList<>(coches)) {
            if (!coche.esVip()) {
                coches.remove(coche);
                cochesDesalojados.incrementAndGet();
                System.out.println(LocalTime.now() + " - " + coche + " fue desalojado por " + cocheVip);
                semaforo.release(); // liberar plaza del desalojado
                break;
            }
        }
    }

    public void mostrarEstadisticas() {
        System.out.println("======ESTADO ESTACIONAMIENTO=====");
        System.out.println("Plazas libres: " + semaforo.availablePermits());
        System.out.println("Coches dentro: " + coches);
        System.out.println("===========");
    }

    public void mostrarEstadisticasFinales() {
        System.out.println("===== ESTADÍSTICAS FINALES =====");
        System.out.println("Coches que lograron entrar: " + cochesAparcaron.get());
        System.out.println("Coches que no lograron entrar: " + cochesFuera.get());
        System.out.println("Coches desalojados: " + cochesDesalojados.get());
    }

    public boolean isActivo() {
        return activo;
    }

    public void detener() {
        activo = false;
    }
}