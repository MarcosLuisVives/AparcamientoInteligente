import java.util.Random;

public class Coche extends Thread {
    private String nombre;
    private boolean vip;
    private Estacionamiento aparcamiento;
    private final Random random = new Random();

    public Coche(String nombre, Estacionamiento aparcamiento, boolean vip) {
        this.nombre = nombre;
        this.aparcamiento = aparcamiento;
        this.vip = vip;
    }

    public boolean esVip() {
        return vip;
    }

    @Override
    public void run() {
        if (!aparcamiento.entrar(this)) {
            System.out.println(nombre + " se marcha, no pudo entrar.");
            return;
        }
        int tiempo = random.nextInt(5) + 2;
        System.out.println(nombre + " está estacionado (" + tiempo + "s).");
        try {
            Thread.sleep(tiempo * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        aparcamiento.salir(this);
    }

    @Override
    public String toString() {
        return vip ? nombre + "[VIP]" : nombre + "[Normal]";
    }
}