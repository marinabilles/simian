package de.crispda.sola.multitester;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tests {
    public static void executeConcurrently(List<Test> tests) throws ExecutionException, InterruptedException {
        final List<ListenableFuture<?>> futures = new ArrayList<>();
        for (Test test : tests) {
            ExecutorService plainES = Executors.newSingleThreadExecutor();
            ListeningExecutorService listeningES = MoreExecutors.listeningDecorator(plainES);
            futures.add(listeningES.submit(test));
            listeningES.shutdown();
        }

        Futures.allAsList(futures).get();
    }
}
