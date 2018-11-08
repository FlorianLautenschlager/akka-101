package de.fla.akka.solution;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import de.fla.akka.common.Index;
import de.fla.akka.common.Pair;
import de.fla.akka.common.Script;
import de.fla.akka.common.Util;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
                    Pair<Script, Script> splits = Util.splitScript(script);
                            first.tell(splits.getFirst(), getSelf());
                            second.tell(splits.getSecond(), getSelf());
                        }
                )
                .match(Index.class, index -> {
                    indexMap.put(getSender(), index);

                    log().info("Got index for '{}' from '{}'", index.getName(), getSender());

                    if (indexMap.containsKey(first) && indexMap.containsKey(second)) {
                        log().info("Got all pieces. Result is {}", Util.mergeIndexOfActors(scriptName, indexMap, first, second));
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

}
