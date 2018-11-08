package de.fla.akka.template;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import de.fla.akka.common.Index;
import de.fla.akka.common.Script;
import de.fla.akka.common.Util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
                            Index index = Util.analyzeChapters(script, keywords);
                            //Send index back to sender
                            //Todo: Send to ScriptSplitter
                        }
                )
                .matchAny(o -> log().info("Unknown message. Ignore and relax."))
                .build();
    }


}
