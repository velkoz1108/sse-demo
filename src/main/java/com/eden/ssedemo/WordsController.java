package com.eden.ssedemo;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sse/mvc")
public class WordsController {
    private static final String[] WORDS = "The quick brown fox jumps over the lazy dog.".split(" ");

    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private SseEmitter emitter;

    @GetMapping(path = "/words", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter getWords() {
        emitter = new SseEmitter();

//        cachedThreadPool.execute(() -> {
//            try {
//                for (int i = 0; i < WORDS.length; i++) {
//                    emitter.send(WORDS[i]);
//                    TimeUnit.SECONDS.sleep(1);
//                }
//
////                emitter.complete();
//            } catch (Exception e) {
//                emitter.completeWithError(e);
//            }
//        });

        return emitter;
    }

    /**
     * 如果调用了complete之后就不能再发了
     * java.lang.IllegalStateException: ResponseBodyEmitter has already completed
     * <p>
     * 一段时间没消息推送了，前端就会断开，需要刷新下才能继续收消息
     *
     * @param msg
     * @return
     * @throws IOException
     */
    @GetMapping("/send")
    @ResponseBody
    public String sendByManual(String msg) throws IOException {
        emitter.send(msg);
        return "OK";
    }

    /**
     * 好像没有啥用
     *
     * @return
     */
    @GetMapping("/close")
    @ResponseBody
    public String close() {
        emitter.complete();
        return "OK";
    }
}
