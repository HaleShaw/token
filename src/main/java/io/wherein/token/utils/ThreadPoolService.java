package io.wherein.token.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolService.
 */
public class ThreadPoolService {

  private static ThreadFactory scheduleThreadFactory = new ThreadFactoryBuilder()
      .setNameFormat("schedule-pool-%d").build();

  private static ExecutorService pool = new ThreadPoolExecutor(4, 10, 0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>(1024), scheduleThreadFactory, new ThreadPoolExecutor.AbortPolicy()
  );

  /**
   * get thread pool.
   *
   * @return pool.
   */
  public static ExecutorService getPool() {
    return pool;
  }

  /**
   * execute a task by pool.
   *
   * @param runnable runnable.
   */
  public static void newTask(Runnable runnable) {
    pool.execute(runnable);
  }
}
