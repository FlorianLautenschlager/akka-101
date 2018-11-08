# Akka 101

Source code for the Actors in Java lecture ([slides](/ActorSystem101.png?raw=true)).

![alt text](/ActorSystem101.png?raw=true)

## Build and Run

Simply import maven projekt into IntelliJ or a tool of your choice.

### Build executable jar and run it

```bash
./mvnw clean compile assembly:single  

java -jar target/akka-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Screenplay

1. Actor System 101: Create Actor Systems
2. ScriptSplitter: Implement receiving + send script and receiving index
3. ScriptSplitter: Implement send index back to ScriptSplitter


### Actor System 101
First Create Actor System:
```
final ActorSystem system = ActorSystem.create(actorSystemName);
```

Then send Message from Root Actor to `ScriptSplitter` Actor
```
scriptSplitter.tell(script, ActorRef.noSender());
```

### ScriptSplitter
First implement receive and send script:
```
log().info("Got script '{}' from {}.", script.getName(), getSender());
scriptName = script.getName();

Pair<Script, Script> splits = Util.splitScript(script);
first.tell(splits.getFirst(), getSelf());
second.tell(splits.getSecond(), getSelf());
```
Then implement receive index:
```
indexMap.put(getSender(), index);

log().info("Got index for '{}' from '{}'", index.getName(), getSender());

if (indexMap.containsKey(first) && indexMap.containsKey(second)) {
     log().info("Got all pieces. Result is {}", Util.mergeIndexOfActors(scriptName, indexMap, first, second));
}
```


### Script Analyzer
Send the index back:
```
 getSender().tell(index, getSelf());
```
