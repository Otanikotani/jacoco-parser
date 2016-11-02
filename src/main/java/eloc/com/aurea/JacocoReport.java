package eloc.com.aurea;

public class JacocoReport {

    private final String name;
    private final int totalLines;

    JacocoReport(String name, int  totalLines) {
        this.name = name;
        this.totalLines = totalLines;
    }

    public String getName() {
        return name;
    }

    public int getTotalLines() {
        return totalLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JacocoReport that = (JacocoReport) o;

        if (totalLines != that.totalLines) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + totalLines;
        return result;
    }
}
