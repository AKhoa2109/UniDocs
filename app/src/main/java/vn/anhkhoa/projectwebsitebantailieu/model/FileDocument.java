package vn.anhkhoa.projectwebsitebantailieu.model;

import vn.anhkhoa.projectwebsitebantailieu.enums.FileType;

public class FileDocument {
    private Long fileId;
    private String fileUrl;
    private FileType fileType;

    private Long docId;

    public FileDocument(){}

    public FileDocument(Long fileId, String fileUrl, FileType fileType, Long docId) {
        this.fileId = fileId;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.docId = docId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }
}
