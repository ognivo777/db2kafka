package org.obiz;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.transactions.KafkaTransactions;
import org.eclipse.microprofile.reactive.messaging.Channel;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import java.time.Duration;
import java.time.Instant;

//@QuarkusMain
@ApplicationScoped
public class LoadTransactionalWithException implements QuarkusApplication {

    @Channel("data")
    KafkaTransactions<String> kafkaTx;

    @Override
    @ActivateRequestContext //без этого не будут работать бины https://quarkus.io/guides/command-mode-reference#contexts
    public int run(String... args) throws Exception {
        System.out.println("Start LoadTransactional");
        Instant started = Instant.now();

        IncomingMessage.<IncomingMessage>streamAll().group().intoLists().of(10000)
                .call(messageList -> {
                    QuarkusTransaction.begin();

                    Uni<Void> resultUni = kafkaTx.withTransaction(emitter -> {
                        for (IncomingMessage message : messageList) {
                            emitter.send(KafkaRecord.of("" + started.getEpochSecond() + message.getId(), message.getId() + ":" + message.getDescr()));
                            message.setProcessed(true);
                        }
                        System.out.println("next batch");
                        return Panache.withTransaction(() -> {
                            for (IncomingMessage incomingMessage : messageList) {
                                IncomingMessage.persist(incomingMessage);
                                if (incomingMessage.getId() > 31111) {
                                    return Uni.createFrom().failure(new Throwable("TEST ERROR"));
                                }
                            }
                            return Uni.createFrom().voidItem();
                        });
                    }).chain(() -> {
                        QuarkusTransaction.commit();
                        return Uni.createFrom().voidItem();
                    });

                    return resultUni;

                })
                .collect()
                .last()
                .await()
                .indefinitely();

        System.out.println("Elapsed: " + Duration.between(started, Instant.now()).getSeconds());
        return 0;
    }
}
