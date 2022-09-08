package com.eden.ssedemo;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sse/mvc")
public class WordsController {
    private static final String[] WORDS = "The quick brown fox jumps over the lazy dog.".split(" ");

    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    @GetMapping(path = "/words", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter getWords() {
        SseEmitter emitter = new SseEmitter();

        cachedThreadPool.execute(() -> {
            try {
                for (int i = 0; i < WORDS.length; i++) {
                    emitter.send(WORDS[i]);
                    TimeUnit.SECONDS.sleep(1);
                }

                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
