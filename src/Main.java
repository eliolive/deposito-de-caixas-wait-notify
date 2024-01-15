import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Deposito {
    private int quantidadeItens;
    private final int capacidadeMaxima;

    public Deposito(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.quantidadeItens = 1; // Inicialmente, o depósito possui uma caixa
    }

    public synchronized void armazenar() throws InterruptedException {
        while (quantidadeItens >= capacidadeMaxima) {
            wait();
        }

        quantidadeItens++;
        System.out.println("Produto armazenado. Itens no depósito: " + quantidadeItens);

        notify();
    }

    public synchronized void retirar() throws InterruptedException {
        while (quantidadeItens <= 0) {
            wait();
        }

        quantidadeItens--;
        System.out.println("Produto retirado. Itens no depósito: " + quantidadeItens);

        notify();
    }

    public synchronized int getQuantidadeItens() {
        return quantidadeItens;
    }
}

class Produtor implements Runnable {
    private final Deposito deposito;
    private final int tempoProducao;

    public Produtor(Deposito deposito, int tempoProducao) {
        this.deposito = deposito;
        this.tempoProducao = tempoProducao;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(tempoProducao);
            deposito.armazenar();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumidor implements Runnable {
    private final Deposito deposito;
    private final int tempoConsumo;

    public Consumidor(Deposito deposito, int tempoConsumo) {
        this.deposito = deposito;
        this.tempoConsumo = tempoConsumo;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(tempoConsumo);
            deposito.retirar();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Deposito deposito = new Deposito(10);

        try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {

            for (int i = 0; i < 10; i++) {
                executorService.execute(new Produtor(deposito, i * 1000));
            }

            for (int i = 0; i < 10; i++) {
                executorService.execute(new Consumidor(deposito, i * 1000));
            }
        }

        System.out.println("Quantidade final de itens no depósito: " + deposito.getQuantidadeItens());
    }
}
