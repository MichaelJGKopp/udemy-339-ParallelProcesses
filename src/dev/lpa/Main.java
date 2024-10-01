package dev.lpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

  public static void main(String[] args) throws Exception {

    int numbersLength = 100_000;
    long[] numbers = new Random().longs(numbersLength,
      1, numbersLength).toArray();

    long sum = Arrays.stream(numbers).sum();
    System.out.println("sum = " + sum);

    ForkJoinPool threadPool = (ForkJoinPool) Executors.newWorkStealingPool(4);
    // parallelism usually set to the number of cpus on your system or less
    // not the number of threads technically

    List<Callable<Long>> tasks = new ArrayList<>();

    int taskNo = 10;
    int splitCount = numbersLength / taskNo;
    for (int i = 0; i < taskNo; i++) {
      int start = i * splitCount;
      int end = start + splitCount;
      tasks.add(() -> {
        long tasksum = 0;
        for (int j = start; j < end; j++) {
          tasksum += (long) numbers[j];
        }
        return tasksum;
      });
    }

    List<Future<Long>> futures = threadPool.invokeAll(tasks);

    long taskSum = 0;
    for (Future<Long> future : futures) {
      taskSum += future.get();
    }

    System.out.println("Thread Pool Sum = " + taskSum);

    threadPool.shutdown();

    System.out.println(threadPool.getClass().getName());
  }
}
