import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    protected Map<String, List<PageEntry>> answer = new HashMap<>();
    protected Set<String> stopWord = new HashSet<>();

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
                        List<PageEntry> pageEntries = new ArrayList<>();
                        if (!answer.containsKey(pair.getKey())) {
                            pageEntries.add(new PageEntry(pdf.getName(), i, pair.getValue()));
                            answer.put(pair.getKey(), pageEntries);
                        } else {
                            pageEntries = answer.get(pair.getKey());
                            pageEntries.add(new PageEntry(pdf.getName(), i, pair.getValue()));
                            answer.put(pair.getKey(), pageEntries);
                            Collections.sort(answer.get(pair.getKey()), PageEntry::compareTo);
                        }
//                        !!!исправление!!!
//                        var word = pair.getKey();
//                        var count = pair.getValue();
//                        List<PageEntry> list = answer.getOrDefault(word, new ArrayList<>());
//                        list.add(new PageEntry(pdf.getName(), i, count));
//                        list.sort(Collections.reverseOrder());
//                        answer.put(word, list);
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {

        List<PageEntry> listNotFirstWord = new ArrayList<>();
        List<PageEntry> allWord = new ArrayList<>();

        String[] wordMassive = word.toLowerCase().split("\\P{IsAlphabetic}+");
        List<String> wordsSearch = new ArrayList<>();
        for (String wordSearch : wordMassive) {
            if (!stopWord.contains(wordSearch)) {
                wordsSearch.add(wordSearch);
            }
        }

        for (String oneWordSearch : wordsSearch) {
            allWord.addAll(answer.get(oneWordSearch));
            break;
        }

        int value = -1;
        for (String oneWordSearch : wordsSearch) {
            if (value == -1) {
                listNotFirstWord = answer.get(oneWordSearch);
                listNotFirstWord.clear();
                value++;
                continue;
            }
            listNotFirstWord = answer.get(oneWordSearch);
            for (PageEntry o : listNotFirstWord) {
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
