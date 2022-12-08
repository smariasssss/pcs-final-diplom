import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.*;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> pageEntryAll = new HashMap<>();
    private Set<String> stopWord =new HashSet<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if (pdfsDir.isDirectory()) {
            for (File item : pdfsDir.listFiles()) {
                var doc = new PdfDocument(new PdfReader(item));
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
                    for (Map.Entry<String, Integer> freqsWord : freqs.entrySet()) {
                        List<PageEntry> pageEntries = new ArrayList<>();
                        if (!pageEntryAll.containsKey(freqsWord.getKey())) {
                            pageEntries.add(new PageEntry(item.getName(), i, freqsWord.getValue()));
                            pageEntryAll.put(freqsWord.getKey(), pageEntries);
                        } else {
                            pageEntries = pageEntryAll.get(freqsWord.getKey());
                            pageEntries.add(new PageEntry(item.getName(), i, freqsWord.getValue()));
                            pageEntryAll.put(freqsWord.getKey(), pageEntries);
                            Collections.sort(pageEntryAll.get(freqsWord.getKey()), PageEntry::compareTo);
                        }
                    }
                }
            }
        }

        try (BufferedReader textFile = new BufferedReader(new FileReader("stop-ru.txt"))) {
            String s;
            while ((s = textFile.readLine()) != null) {
                stopWord.add(s);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PageEntry> search(String word) {

        List<PageEntry> listNoFirstWord = new ArrayList<>();
        List<PageEntry> allWord = new ArrayList<>();

        String[] wordMassive = word.toLowerCase().split("\\P{IsAlphabetic}+");
        List<String> wordsSearch = new ArrayList<>();
        for (String wordSearch : wordMassive) {
            if (!stopWord.contains(wordSearch)) {
                wordsSearch.add(wordSearch);
            }
        }

        for (String oneWordSearch : wordsSearch) {
            allWord.addAll(pageEntryAll.get(oneWordSearch));
            break;
        }

        int value = -1;
        for (String oneWordSearch : wordsSearch) {
            if (value == -1) {
                listNoFirstWord = pageEntryAll.get(oneWordSearch);
                listNoFirstWord.clear();
                value++;
                continue;
            }
            listNoFirstWord = pageEntryAll.get(oneWordSearch);
            for (PageEntry o : listNoFirstWord) {
                value = 0;
                for (PageEntry o1 : allWord) {
                    if (o.getPdfName().equals(o1.getPdfName()) && o.getPage() == o1.getPage()) {
                        allWord.remove(o1);
                        allWord.add(new PageEntry(o1.getPdfName(), o1.getPage(), (o.getCount() + o1.getCount())));
                        value++;
                        break;
                    }
                }
                if (value == 0) {
                    allWord.add(o);
                }
            }
        }

        allWord.sort(PageEntry::compareTo);

        if (allWord != null) {
            return allWord;
        } else {
            return Collections.emptyList();
        }
    }
}
