package com.csl.singleton;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author MaoLongLong
 * @date 2021-03-16 22:32:32
 */
public class SingletonTest<S> {

    private final Supplier<S> singletonInstanceMethod;

    public SingletonTest(Supplier<S> singletonInstanceMethod) {
        this.singletonInstanceMethod = singletonInstanceMethod;
    }

    @Test
    void testSerial() {
        var s1 = LazySingleton.getInstance();
        var s2 = LazySingleton.getInstance();
        assertNotNull(s1);
        assertNotNull(s2);
        assertSame(s1, s2);
    }

    @Test
    void testConcurrent() {
        // TODO: 在我的机子上不知道为什么复现不了普通懒汉和额汉在多线程下的错误
        assertTimeout(ofSeconds(10), () -> {

            // 模拟 10000 次 getInstance
            var tasks = IntStream.range(0, 10000)
                    .<Callable<S>>mapToObj(i -> this.singletonInstanceMethod::get)
                    .collect(Collectors.toList());

            // 在 8 个线程的环境下执行
            var es = Executors.newFixedThreadPool(8);
            var results = es.invokeAll(tasks);

            var expected = this.singletonInstanceMethod.get();
            for (Future<S> result : results) {
                var instance = result.get();
                assertNotNull(instance);
                assertSame(expected, instance);
            }

            es.shutdown();
        });
    }
}
