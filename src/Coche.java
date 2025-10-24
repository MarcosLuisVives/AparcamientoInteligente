import java.util.Random;

public class Coche extends Thread {
    private final String nombre;
    private final boolean vip;
    private final Estacionamiento estacionamiento;
    private final Random random = new Random();

    public Coche(String nombre, Estacionamiento estacionamiento, boolean vip) {
        this.nombre = nombre;
        this.estacionamiento = estacionamiento;
        this.vip = vip;
    }

    public boolean esVip() {
        return vip;
    }

    @Override
    public void run() {
        if (!estacionamiento.entrar(this)) {
            System.out.println(this + " se marcha, no pudo entrar.");
            return;
        }

        int tiempo = random.nextInt(5) + 2; // 2 a 6 segundos
        System.out.println(this + " está estacionado (" + tiempo + "s).");
        try {
            Thread.sleep(tiempo * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        estacionamiento.salir(this);
    }

    @Override
    public String toString() {
        return (vip ? "[VIP] " : "[Normal] ") + nombre;
    }
}
