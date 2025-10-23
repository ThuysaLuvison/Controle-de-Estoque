public class controleEstoque {
    public static void main(String[] args) {
        Estoque estoque = new Estoque(5); // estoque inicial com 5 unidades

        // Criação de duas threads de venda
        Thread vendedor1 = new Thread(new Venda(estoque, "Vendedor-1"));
        Thread vendedor2 = new Thread(new Venda(estoque, "Vendedor-2"));

        // Criação da thread de reposição
        Thread repositor = new Thread(new Reposicao(estoque, "Repositor"));

        // Inicia as threads
        vendedor1.start();
        vendedor2.start();
        repositor.start();

        // Aguarda todas as threads finalizarem
        try {
            vendedor1.join();
            vendedor2.join();
            repositor.join();
        } catch (InterruptedException e) {
            System.out.println("Execução interrompida: " + e.getMessage());
        }

        System.out.println("\nSimulação encerrada!");
    }
}

// Classe que representa o estoque compartilhado
class Estoque {
    private int quantidade;

    public Estoque(int quantidadeInicial) {
        this.quantidade = quantidadeInicial;
    }

    // Método synchronized para vender
    public synchronized void vender(String nomeVendedor) {
        while (quantidade == 0) {
            try {
                System.out.println(nomeVendedor + " aguardando reposição...");
                wait(); // aguarda até que haja reposição
            } catch (InterruptedException e) {
                System.out.println(nomeVendedor + " interrompido durante a espera.");
                return;
            }
        }

        quantidade--;
        System.out.println(nomeVendedor + " realizou uma venda. Estoque restante: " + quantidade);
    }

    // Método synchronized para repor o estoque
    public synchronized void repor(String nomeRepositor, int qtd) {
        quantidade += qtd;
        System.out.println(nomeRepositor + " repôs " + qtd + " unidades. Estoque atual: " + quantidade);
        notifyAll(); // notifica todas as threads que estavam aguardando
    }
}

// Classe que simula uma thread de venda
class Venda implements Runnable {
    private final Estoque estoque;
    private final String nome;

    public Venda(Estoque estoque, String nome) {
        this.estoque = estoque;
        this.nome = nome;
    }

    @Override
    public void run() {
        for (int i = 0; i < 8; i++) { // cada vendedor tenta vender 8 vezes
            estoque.vender(nome);
            try {
                Thread.sleep(1000); // pausa entre as vendas
            } catch (InterruptedException e) {
                System.out.println(nome + " interrompido.");
                return;
            }
        }
        System.out.println(nome + " encerrou suas vendas.");
    }
}

// Classe que simula uma thread de reposição
class Reposicao implements Runnable {
    private final Estoque estoque;
    private final String nome;

    public Reposicao(Estoque estoque, String nome) {
        this.estoque = estoque;
        this.nome = nome;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) { // fará 5 reposições
            try {
                Thread.sleep(3000); // pausa entre reposições
            } catch (InterruptedException e) {
                System.out.println(nome + " interrompido.");
                return;
            }

            int qtdReposicao = (int) (Math.random() * 5 + 1); // repõe de 1 a 5 unidades
            estoque.repor(nome, qtdReposicao);
        }
        System.out.println(nome + " encerrou as reposições.");
    }
}
