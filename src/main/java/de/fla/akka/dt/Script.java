package de.fla.akka.dt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Script {

    private final String name;
    private final Map<Integer, String> chapters;

    /**
     * @param name           of the script
     * @param chapterContent pairs of chapter and content
     */
    public Script(String name, Pair<Integer, String>... chapterContent) {

        if (chapterContent == null) {
            throw new IllegalStateException("Chapter content is 'null' or has invalid format.");
        }

        this.name = name;
        chapters = new HashMap<>();

        for (Pair<Integer, String> pair : chapterContent) {
            chapters.put(pair.getFirst(), pair.getSecond());
        }

    }

    public String getName() {
        return name;
    }

    public Map<Integer, String> getChapters() {
        return chapters;
    }

    public int size() {
        return chapters.size();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Script{");
        sb.append("name='").append(name).append('\'');
        sb.append(", chapters=").append(chapters);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Script script = (Script) o;
        return Objects.equals(name, script.name) &&
                Objects.equals(chapters, script.chapters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chapters);
    }

    public Script getChapters(int start, int end) {

        if (end - start <= 0) {
            throw new IllegalArgumentException("Defined range is invalid");
        }

        int length = end - start;

        Pair[] result = new Pair[length];

        for (int i = 0, j = start; i < length; i++, j++) {
            result[i] = Pair.of(j, chapters.get(j));
        }
        return new Script(this.name + " Chapters:{" + start + "-" + end + "}", result);
    }
}
