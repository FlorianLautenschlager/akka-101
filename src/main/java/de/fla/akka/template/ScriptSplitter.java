package de.fla.akka.template;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import de.fla.akka.common.Index;
import de.fla.akka.common.Script;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ScriptSplitter extends AbstractLoggingActor {

    private final ActorRef first = getContext().actorOf(ScriptAnalyzer.props("Classes", "Interfaces", "Predicate", "Idea"),
            "ScriptAnalyzer1");
    private final ActorRef second = getContext().actorOf(ScriptAnalyzer.props("Datatypes", "Components", "Logging", "Threads"),
            "ScriptAnalyzer2");

    //Variables (state)
    private final Map<ActorRef, Index> indexMap = new HashMap<>();
    private String scriptName;

    static Props props() {
        return Props.create(ScriptSplitter.class, ScriptSplitter::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                //TODO: Implement split and send script.
                .match(Script.class, script -> {

                        }
                )
                //TODO: Implement receiving messages.
                .match(Index.class, index -> {

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
