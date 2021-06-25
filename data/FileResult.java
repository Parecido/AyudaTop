package topologyreader.data;

import topologyreader.data.type.FileType;

import java.io.File;
import java.nio.file.Path;

public class FileResult {
    private int index;
    private final FileType type;
    private final Path path;

    public FileResult(FileType type, Path path) {
        this.type = type;
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FileType getType() {
        return type;
    }

    public File getFile() {
        return path.toFile();
    }

    public String getName() {
        return path.getFileName().toString();
    }
}
