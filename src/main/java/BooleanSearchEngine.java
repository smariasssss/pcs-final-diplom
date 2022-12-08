import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    protected Map<String, List<PageEntry>> answer = new HashMap<>();

    public BooleanSearchEngine(File pdfs) throws IOException {

        if (pdfs.listFiles() != null) {
            for (File pdf : pdfs.listFiles()) {
                var doc = new PdfDocument(new PdfReader(pdf));

                for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    var words = text.split("\\P{IsAlphabetic}+");

                    Map<String, Integer> freqs = new HashMap<>();
                    for (var word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }

                    for (Map.Entry<String, Integer> pair : freqs.entrySet()) {
                        var word = pair.getKey();
                        var count = pair.getValue();
                        List<PageEntry> list = answer.getOrDefault(word, new ArrayList<>());
                        list.add(new PageEntry(pdf.getName(), i, count));
                        list.sort(Collections.reverseOrder());
                        answer.put(word, list);
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> listAnswer = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("stop-ru.txt"))) {
            String string;
            List<String> stopWords = new ArrayList<>();
            while ((string = reader.readLine()) != null) {
                stopWords.add(string);
            }

            String[] words = word.toLowerCase().split("\\P{IsAlphabetic}+");
            List<String> newWords = new ArrayList<>();
            for (String s : words) {
                if (!stopWords.contains(s)) {
                    newWords.add(s);
                }
            }

            listAnswer.addAll(answer.get(newWords.get(0)));
            for (int i = 1; i < newWords.size(); i++) {
                List<PageEntry> listWordTwo = new ArrayList<>(answer.get(newWords.get(i)));

                for (PageEntry pageWordTwo : listWordTwo) {
                    int counter = 0;

                    for (PageEntry pageAnswer : listAnswer) {
                        if (pageAnswer.samePage(pageWordTwo)) {
                            String pdfName = pageAnswer.getPdfName();
                            int page = pageAnswer.getPage();
                            int count = pageAnswer.getCount() + pageWordTwo.getCount();
                            listAnswer.remove(pageAnswer);
                            listAnswer.add(new PageEntry(pdfName, page, count));
                            counter++;
                            break;
                        }
                    }
                    if (counter == 0) {
                        listAnswer.add(pageWordTwo);
                    }
                }
            }
            listAnswer.sort(Collections.reverseOrder());

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return listAnswer;
    }
}
