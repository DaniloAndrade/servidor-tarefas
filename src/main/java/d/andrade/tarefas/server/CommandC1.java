package d.andrade.tarefas.server;

import java.io.PrintStream;

public class CommandC1 implements Runnable {

  private final PrintStream saida;

  public CommandC1(PrintStream saida) {
    this.saida = saida;
  }

  @Override
  public void run() {

    System.out.println("Executando comando c1");

    try {
      // faz algo bem demorado
      Thread.sleep(20000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    //devolvendo resposta para o cliente
    saida.println("Comando c1 executado com sucesso!");
  }
}
