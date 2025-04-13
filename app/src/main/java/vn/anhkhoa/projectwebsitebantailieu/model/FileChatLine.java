package vn.anhkhoa.projectwebsitebantailieu.model;

import vn.anhkhoa.projectwebsitebantailieu.enums.FileType;

public class FileChatLine {
    private Long fileId;
    private String fileUrl;
    private FileType fileType;
    private Long chatLineId;

    private Long size;

    private String name="File không có tên";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    // Constructor rỗng
    public FileChatLine() {}


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
