import java.util.*;
import java.util.concurrent.*;
import java.io.*;



public class RideSharingSystem {
    private static final Queue<String> taskQueue = new LinkedList<>();
    private static final List<String> results = Collections.synchronizedList(new ArrayList<>());
    private static final int NUM_WORKERS = 4;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        // Populate tasks
        for (int i = 1; i <= 10; i++) {
            addTask("Ride Request #" + i);
        }

        // Start worker threads
        ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKERS);
        for (int i = 1; i <= NUM_WORKERS; i++) {
            int id = i;
            executor.submit(() -> workerTask(id));
        }

        // Shutdown executor
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Thread termination interrupted: " + e.getMessage());
        }

        // Print results
        System.out.println("============All ride requests processed:===========");
        for (String res : results) {
            System.out.println(res);
        }
    }

    private static void addTask(String task) {
        synchronized (lock) {
            taskQueue.offer(task);
        }
    }

    private static String getTask() {
        synchronized (lock) {
            return taskQueue.poll();
        }
    }

    private static void workerTask(int id) {
        String task;
        System.out.println("Worker " + id + " started.");
        while (true) {
            try {
                task = getTask();
                if (task == null) break;

                // Simulated processing delay
                Thread.sleep(500);

                String result = "Worker " + id + " processed: " + task;
                results.add(result);
            } catch (InterruptedException e) {
                System.err.println("Worker " + id + " interrupted.");
                break;
            } catch (Exception e) {
                System.err.println("Worker " + id + " error: " + e.getMessage());
            }
        }
        System.out.println("Worker " + id + " completed.");
    }
}
