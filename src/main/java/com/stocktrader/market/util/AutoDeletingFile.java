package com.stocktrader.market.util;

import java.io.File;

public class AutoDeletingFile implements AutoCloseable {

    private final File file;

    public AutoDeletingFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public void close() {
        file.delete();
    }
}
