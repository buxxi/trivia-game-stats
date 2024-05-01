package se.omfilm.trivia.stats.infrastructure;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ClearCacheOnFileChange {
    private final StatsFilesInfrastructure statsFilesInfrastructure;
    private final ExecutorService executorService;

    @Autowired
    public ClearCacheOnFileChange(StatsFilesInfrastructure statsFilesInfrastructure) {
        this.statsFilesInfrastructure = statsFilesInfrastructure;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    public void init() {
        executorService.submit(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                statsFilesInfrastructure.getRootPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    key.pollEvents().forEach((i) -> statsFilesInfrastructure.clearCache());
                    key.reset();
                }
            } catch (Exception ignored) {}
        });

    }
}
