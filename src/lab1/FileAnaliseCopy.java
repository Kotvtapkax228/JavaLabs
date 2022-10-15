package lab1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class FileAnaliseCopy {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Scanner scanner = new Scanner(System.in);
        File file;
        List<Callable<String>> callableList = new ArrayList<>();
        do {
            System.out.println("Введи назву папки: ");
            file = new File(scanner.next());
            if (!file.exists()) {
                System.out.println("Такої папки не існує");
            }
        }
        while (!file.exists());

        analise(file, executorService, callableList);
        List<Future<String>> futures = executorService.invokeAll(callableList);
        for (int i = 0; i < futures.size(); i++) {
            System.out.println(futures.get(i).get());
        }
        executorService.shutdown();
    }

    public static void analise(File file, ExecutorService executorService, List<Callable<String>> callableList) {
        if (file.isFile()) {
//            Callable<String> callable = () -> file.getName();
            Runnable runnable = () -> System.out.println(file.getName());
            executorService.execute(runnable);
//            callableList.add(callable);

            if (file.getAbsolutePath().endsWith(".txt")) {
                try {
                    String ex = Files.lines(file.toPath()).flatMap((h) -> Arrays.stream(h.split(" ")))
                            .filter((n) -> !(n.length() >= 3 && n.length() <= 5))
                            .reduce((j, k) -> j + " " + k)
                            .get();
                    Files.deleteIfExists(file.toPath());
                    Files.writeString(file.toPath(), ex, new OpenOption[]{StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE});
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        } else {
            Callable<String> callable = () -> file.getName();
            callableList.add(callable);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                analise(files[i], executorService, callableList);
            }
        }
    }
}
