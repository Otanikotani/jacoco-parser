package eloc.com.aurea;

import com.google.common.collect.Range;

public class Duplicate {
    private final int times;
    private final int group;
    private final Range<Integer> lines;
    private final String className;

    Duplicate(int times, Range<Integer> lines, int group, String className) {
        this.times = times;
        this.lines = lines;
        this.group = group;
        this.className = className;
    }

    public int getTimes() {
        return times;
    }

    public int getGroup() {
        return group;
    }

    public Range<Integer> getLines() {
        return lines;
    }

    public String getClassName() {
        return className;
    }

    public int distance() {
        return lines.upperEndpoint() - lines.lowerEndpoint();
    }

    @Override
    public String toString() {
        return times + " x " + lines;
    }

}
