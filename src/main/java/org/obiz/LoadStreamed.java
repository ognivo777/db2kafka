package org.obiz;

import io.quarkus.runtime.QuarkusApplication;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import org.eclipse.microprofile.reactive.messaging.Channel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

//@QuarkusMain
@ApplicationScoped
public class LoadStreamed implements QuarkusApplication {

    @Inject
    @Channel("data1")
    MutinyEmitter<String> emitter;


    @Override
    @ActivateRequestContext //без этого не будут работать бины https://quarkus.io/guides/command-mode-reference#contexts
    public int run(String... args) throws InterruptedException {
        System.out.println("Start LoadStreamed");
        Instant started = Instant.now();

        emitter.send("==========PLACEHOLDER==========" + started.toString()).await().indefinitely();

        IncomingMessage.<IncomingMessage>streamAll().invoke(message -> {
                    emitter.sendAndForget(message.getId() + ":" + message.getDescr());
                })
                .collect()
                .last()
                .await()
                .indefinitely();

        System.out.println("Elapsed: " + Duration.between(started, Instant.now()).getSeconds());
        return 0;
    }
}
