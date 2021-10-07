package com.stocktrader.market.util;

import java.io.File;

public class AutoDeletingFile implements AutoCloseable {

    private final File file;
    private final String format;

    public AutoDeletingFile(File file, String format) {
        this.file = file;
        this.format = format;
    }

    public File getFile() {
        return file;
    }

    @Override
    public void close() {
        file.delete();
    }

    public String getFormat() {
        return this.format;
    }
}
