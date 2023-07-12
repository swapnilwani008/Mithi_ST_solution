import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

class BookIndexer{
    private Map<String, Set<Integer>> wordIndex;
    public BookIndexer() {
        wordIndex = new TreeMap<>();
    }

        public void buildIndex(List<String> pages, List<String> excludeWords) {
        for (int i = 0; i < pages.size(); i++) {
            String pageContent = pages.get(i);
            String[] words = pageContent.split("\\s+");

            for (String word : words) {
                word = word.toLowerCase().replaceAll("[^a-z]", "");

                if (!word.isEmpty() && !excludeWords.contains(word)) {
                    Set<Integer> pageNumbers = wordIndex.computeIfAbsent(word, k -> new HashSet<>());
                    pageNumbers.add(i + 1);
                }
            }
        }
    }


    public void writeIndexToFile(String outputFilePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            for (Map.Entry<String, Set<Integer>> entry : wordIndex.entrySet()) {
                String word = entry.getKey();
                Set<Integer> pageNumbers = entry.getValue();

                writer.print(word + " : ");

                String pageList = pageNumbers.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","));

                writer.println(pageList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class BookIndexingApp {
    public static void main(String[] args) {
        List<String> pages = new ArrayList<>();
        List<String> excludeWords = new ArrayList<>();

        try {
            pages.add(readFile("Page1.txt"));
            pages.add(readFile("Page2.txt"));
            pages.add(readFile("Page3.txt"));

            excludeWords = readWordsFromFile("exclude-words.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        BookIndexer indexer = new BookIndexer();
        indexer.buildIndex(pages, excludeWords);
        indexer.writeIndexToFile("index.txt");
    }

    private static String readFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    private static List<String> readWordsFromFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath)).stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
