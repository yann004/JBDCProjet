package JavaDb;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;

public class SensorDataQueue {

    private static final ConcurrentLinkedQueue<Map<String, String>> queue = new ConcurrentLinkedQueue<>();

    public static void addToQueue(Map<String, String> data) {
        queue.add(data);
    }

    public static Map<String, String> removeFromQueue() {
        return queue.poll(); // Récupère et supprime l'élément en tête de file
    }

    public static void printQueue() {
        System.out.println("Current queue contents:");
        Object[] array = queue.toArray();
        for (Object obj : array) {
            System.out.println(obj);
        }
    }
    
    
}
