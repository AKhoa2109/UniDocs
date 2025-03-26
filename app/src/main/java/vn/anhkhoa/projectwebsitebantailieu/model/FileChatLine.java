package vn.anhkhoa.projectwebsitebantailieu.model;

import vn.anhkhoa.projectwebsitebantailieu.enums.FileType;

public class FileChatLine {
    private Long fileId;
    private String fileUrl;
    private FileType fileType;
    private Long chatLineId;

    // Constructor rỗng
    public FileChatLine() {}

    // Constructor đầy đủ
    public FileChatLine(Long fileId, String fileUrl, FileType fileType, Long chatLineId) {
        this.fileId = fileId;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.chatLineId = chatLineId;
    }

    // Getter và Setter
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

    public Long getChatLineId() {
        return chatLineId;
    }

    public void setChatLineId(Long chatLineId) {
        this.chatLineId = chatLineId;
    }
}
