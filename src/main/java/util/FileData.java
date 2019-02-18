package util;

import java.util.Objects;

public class FileData {

    private final String fileId;
    private final String fileMimeType;

    public FileData(final String fileId, final String fileMimeType) {
        this.fileId = fileId;
        this.fileMimeType = fileMimeType;
    }

    public String fileId() {
        return this.fileId;
    }

    public String fileMimeType() {
        return this.fileMimeType;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            FileData fileData = (FileData)o;
            return Objects.equals(this.fileId(), fileData.fileId()) && Objects.equals(this.fileMimeType(), fileData.fileMimeType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.fileId(), this.fileMimeType()});
    }

    public String toString() {
        return "FileData{fileId='" + this.fileId + '\'' + ", fileMimeType='" + this.fileMimeType + '\'' + '}';
    }
}
