package d.andrade.tarefas.server;

import java.util.concurrent.ThreadFactory;

public class FabricaDeThread implements ThreadFactory {

  private static int numero = 1;

  private final ThreadFactory defaultThreadFactory;

  public FabricaDeThread(ThreadFactory defaultThreadFactory) {
    this.defaultThreadFactory = defaultThreadFactory;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = defaultThreadFactory.newThread(r);
    numero ++;
    thread.setUncaughtExceptionHandler(new TratadorDeExceptions());
    return thread;
  }
}
