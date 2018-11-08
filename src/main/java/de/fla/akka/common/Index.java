package de.fla.akka.common;

import java.util.*;

public class Index {

    private final String name;
    private final Map<String, List<Integer>> index;

    /**
     * @param scriptName of the script
     * @param index      index of keywords
     */
    public Index(String scriptName, Map<String, List<Integer>> index) {
        this.name = scriptName;
        this.index = index;
    }

    public Set<String> getKeyWords() {
        return index.keySet();
    }

    public List<Integer> getChapters(String keyword) {
        List<Integer> chapters = index.get(keyword);
        if (chapters == null) {
            return new ArrayList<>();
        }
        return chapters;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return Objects.equals(name, index.name) &&
                Objects.equals(this.index, index.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, index);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Index{");
        sb.append("name='").append(name).append('\'');
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
