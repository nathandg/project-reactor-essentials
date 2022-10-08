package org.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Main {

    @Test
    public void monoSubscriber() {
        String name = "Willian Suane";

        Mono<String> mono = Mono.just(name).log();

        mono.subscribe();

        log.info("--------------------------------------------------");
        StepVerifier.create(mono)
                        .expectNext(name)
                        .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "Willian Suane";

        Mono<String> mono = Mono.just(name).log();

        mono.subscribe(s -> log.info("Value {}", s));

        log.info("--------------------------------------------------");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerError() {
        String name = "Willian Suane";

        Mono<String> mono = Mono.just(name)
                .map(s -> {throw new RuntimeException("Testing mono with error");});

        //print this error
        mono.subscribe(s -> log.info("Name {}", s), s -> log.error("Something bad happened"));
        //print the stacktrace for this error
        mono.subscribe(s -> log.info("Name {}", s), Throwable::printStackTrace);

        log.info("--------------------------------------------------");
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String name = "Willian Suane";

        // transform the mono to UpperCase
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        //print a mono value
        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));

        log.info("--------------------------------------------------");
        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerSubscription() {
        String name = "Willian Suane";

        // transform the mono to UpperCase
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);

        //print a mono value
        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"),
                Subscription::cancel);

        /* brake pressure with a subscription
        mono.subscribe(s -> log.info("Value {}", s),
        Throwable::printStackTrace,
        () -> log.info("FINISHED!"),
        subscription -> subscription.request(5));
        */

        log.info("--------------------------------------------------");
        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    @Test
    public void monoDoOnMethods() {

        String name = "Nikola Tesla";
        Mono<Object> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(longNumber -> log.info("Request received, starting doing something..."))
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s)) //will not be executed
                .doOnSuccess(s -> log.info("doOnSuccess executed {}", s));

        //print a mono value
        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));

        log.info("--------------------------------------------------");
        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();
    }

    @Test
    public void monoDoOnError() {

        //Do not execute after .doOnError
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .doOnNext(s -> log.info("Executing this doOnNext {}", s))
                .log();


        StepVerifier.create(error)
                .expectError(IllegalArgumentException.class)
                .verify();

    }

    @Test
    public void monoDoOnErrorResume() {

        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .onErrorResume(s -> {
                    log.info("Inside OnErrorResume");
                    return Mono.just("I am return a value");
                })
                .log();


        StepVerifier.create(error)
                .expectNext("I am return a value")
                .verifyComplete();

    }

    @Test
    public void monoDoOnErrorReturn() {

        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
                .onErrorReturn("I am return a value")
                .doOnError(e -> log.error("Error message: {}", e.getMessage()))
                .log();


        StepVerifier.create(error)
                .expectNext("I am return a value")
                .verifyComplete();

    }



}