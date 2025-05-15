package vn.anhkhoa.projectwebsitebantailieu.model;

import vn.anhkhoa.projectwebsitebantailieu.enums.FileType;

public class FileMedia {
    Long fileId;
    String fileUrl;
    FileType fileType;

    public FileMedia() {
    }

    public FileMedia(Long fileId, FileType fileType, String fileUrl) {
        this.fileId = fileId;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
