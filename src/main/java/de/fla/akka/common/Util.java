package de.fla.akka.common;

import akka.actor.ActorRef;

import java.util.*;

public class Util {

    private Util() {
        //avoid instances
    }

    public static Index analyzeChapters(Script script, Set<String> keywords) {

        Map<String, List<Integer>> index = new HashMap<>();

        for (String keyword : keywords) {
            for (Map.Entry<Integer, String> chapterContent : script.getChapters().entrySet()) {
                if (chapterContent.getValue().toLowerCase().contains(keyword.toLowerCase())) {
                    if (!index.containsKey(keyword)) {
                        index.put(keyword, new ArrayList<>());
                    }

                    index.get(keyword).add(chapterContent.getKey());
                }
            }
        }

        return new Index(script.getName(), index);
    }

    public static Index mergeIndexOfActors(String scriptName, Map<ActorRef, Index> indexMap, ActorRef first, ActorRef second) {

        Index scriptAnalyzer1 = indexMap.get(first);
        Index scriptAnalyzer2 = indexMap.get(second);

        Set<String> keywords = new HashSet<String>() {{
            addAll(scriptAnalyzer1.getKeyWords());
            addAll(scriptAnalyzer2.getKeyWords());
        }};


        Map<String, List<Integer>> result = new HashMap<>();

        for (String keyword : keywords) {

            if (!result.containsKey(keyword)) {
                result.put(keyword, new ArrayList<>());
            }

            List<Integer> chapters = new ArrayList<>();

            chapters.addAll(scriptAnalyzer1.getChapters(keyword));
            chapters.addAll(scriptAnalyzer2.getChapters(keyword));

            result.get(keyword).addAll(chapters);
        }

        return new Index(scriptName, result);

    }

    public static Pair<Script, Script> splitScript(Script script) {

        int lastElement = (int) Math.floor(script.size() / 2d);

        Script partOne = script.getChapters(1, lastElement);
        Script partTwo = script.getChapters(lastElement + 1, script.size());

        return Pair.of(partOne, partTwo);
    }
}
