package com.fachada.cagepa.fachadacagepa.domain.enterprise.observer;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.interfaces.ImageObserver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ImageWatcher {
    private final List<ImageObserver> observers = new ArrayList<>();
    private final WatchService watchService;
    private final Path dir;
    private volatile boolean running = false;

    public ImageWatcher(String directoryPath) throws IOException {
        this.dir = Paths.get(directoryPath);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Diretório inválido: " + directoryPath);
        }
        this.watchService = FileSystems.getDefault().newWatchService();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public void addObserver(ImageObserver observer) {
        observers.add(observer);
    }

    public void startWatching() {
        if (running) return;
        running = true;
        new Thread(() -> {
            try {
                WatchKey key;
                while (running && (key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path fileName = (Path) event.context();
                        String fullPath = dir.resolve(fileName).toString();
                        if (fullPath.endsWith(".png") || fullPath.endsWith(".jpg") || fullPath.endsWith(".jpeg")) {
                            observers.forEach(o -> o.onImageEvent(fullPath, event.kind()));
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                try {
                    watchService.close();
                } catch (IOException ignored) {}
            }
        }, "ImageWatcher-Thread").start();
    }

    public void stopWatching() {
        running = false;
    }
}
