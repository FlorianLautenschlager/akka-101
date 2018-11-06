package de.fla.akka;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import de.fla.akka.dt.Index;
import de.fla.akka.dt.Pair;
import de.fla.akka.dt.Script;

import java.time.Duration;
import java.util.*;

public class ScriptSplitter extends AbstractLoggingActor {

    private final ActorRef first = getContext().actorOf(ScriptAnalyzer.props("Classes", "Interfaces", "Predicate", "Idea"), "ScriptAnalyzer1");
    private final ActorRef second = getContext().actorOf(ScriptAnalyzer.props("Datatypes", "Components", "Logging", "Threads"), "ScriptAnalyzer2");

    private final Map<ActorRef, Index> indexMap = new HashMap<>();
    private String scriptName;

    static Props props() {
        return Props.create(ScriptSplitter.class, ScriptSplitter::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Script.class, script -> {
                            log().info("Got script '{}' from {}.", script.getName(), getSender());
                            scriptName = script.getName();

                            //Split script
                            Pair<Script, Script> splits = split(script);
                            first.tell(splits.getFirst(), getSelf());
                            second.tell(splits.getSecond(), getSelf());
                        }
                )
                .match(Index.class, index -> {
                    indexMap.put(getSender(), index);

                    log().info("Got index for '{}' from '{}'", index.getName(), getSender());

                    if (indexMap.containsKey(first) && indexMap.containsKey(second)) {
                        log().info("Got all pieces. Result is {}", merge(indexMap));
                    }
                })
                .matchAny(o -> log().info("Got unknown message."))
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(10, Duration.ofMinutes(1),
                DeciderBuilder.match(NullPointerException.class, e -> SupervisorStrategy.restart())
                        .matchAny(o -> SupervisorStrategy.escalate())
                        .build());
    }

    private Index merge(Map<ActorRef, Index> indexMap) {

        Index scriptAnalyzer1 = indexMap.get(this.first);
        Index scriptAnalyzer2 = indexMap.get(this.second);

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

    private Pair<Script, Script> split(Script script) {

        int index = (int) Math.floor(script.size() / 2d);

        Script partOne = script.getChapters(1, index);
        Script partTwo = script.getChapters(index, script.size());

        return Pair.of(partOne, partTwo);
    }

}
