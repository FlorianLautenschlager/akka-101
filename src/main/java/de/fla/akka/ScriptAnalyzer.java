package de.fla.akka;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import de.fla.akka.dt.Index;
import de.fla.akka.dt.Script;

import java.util.*;

public class ScriptAnalyzer extends AbstractLoggingActor {

    private final Set<String> keywords;

    private ScriptAnalyzer(String... keywords) {
        this.keywords = new HashSet<>(Arrays.asList(keywords));
    }

    static Props props(String... keywords) {
        return Props.create(ScriptAnalyzer.class, () -> new ScriptAnalyzer(keywords));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Script.class, script -> {
                            log().info("Got script '{}' from '{}'", script.getName(), getSender());
                            Index index = analyzeChapters(script);
                            //Send index back to sender
                            getSender().tell(index, getSelf());
                        }
                )
                .matchAny(o -> log().info("Unknown message. Ignore and relax."))
                .build();
    }

    private Index analyzeChapters(Script script) {

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
}
