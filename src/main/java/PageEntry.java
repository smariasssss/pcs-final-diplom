public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry p) {
        return Integer.compare(count, p.count);
    }

    @Override
    public String toString() {
        return "\nPageEntry{" +
                "'pdfName'=" + pdfName +
                ",\n'page'=" + page +
                ",\n'count'=" + count +
                '}'+"\n";
    }

    public Boolean samePage(PageEntry page) {
        return (page.pdfName.equals(this.pdfName)) && (page.page == this.page);
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }
}
