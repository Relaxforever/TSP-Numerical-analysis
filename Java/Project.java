import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Project {

    private static double[][] distances;
    private static List<double[]> coordinates = new ArrayList<>();

    public static void main(String[] args) {
        try {
            readDistances("distances.txt");
            readCoordinates("coordinates.txt");
            long startTime = System.nanoTime();
            int[] tour = nearestNeighborTSP(distances);
            long endTime = System.nanoTime();
            System.out.println("Execution time in seconds: " + (endTime - startTime)/ 1_000_000_000.0);
            printTour(tour, coordinates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] nearestNeighborTSP(double[][] distances) {
        int n = distances.length;
        int[] tour = new int[n + 1];
        boolean[] visited = new boolean[n];

        tour[0] = 0;
        visited[0] = true;

        for (int i = 1; i < n; i++) {
            int last = tour[i - 1];
            int next = -1;
            double minDist = Double.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (!visited[j] && distances[last][j] < minDist) {
                    next = j;
                    minDist = distances[last][j];
                    System.out.println("City " + last + " to City " + next + ": " + minDist);
                }
            }

            tour[i] = next;
            visited[next] = true;
        }

        tour[n] = tour[0];
        return tour;
    }

    private static void printTour(int[] tour, List<double[]> coordinates) {
        System.out.println("Tour:");
        double totalDistance = 0;
        for (int city : tour) {
            if(city != tour[0]){
                totalDistance = totalDistance + distances[tour[city-1]][tour[city]];
            } 
            System.out.printf("City %d: (%.4f, %.4f)%n", city, coordinates.get(city)[0], coordinates.get(city)[1]);
        }
        System.out.println("Total distance: " + totalDistance);
    }

    private static void readDistances(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        int n = lines.size();
        distances = new double[n][n];

        for (int i = 0; i < n; i++) {
            String[] parts = lines.get(i).split("\\s+");
            for (int j = 0; j < n; j++) {
                distances[i][j] = Double.parseDouble(parts[j]);
            }
        }
    }

    private static void readCoordinates(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (String line : lines) {
            String[] parts = line.split("\\s+");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            coordinates.add(new double[]{x, y});
        }
    }

    private static void runCommand(String command) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
    }

}