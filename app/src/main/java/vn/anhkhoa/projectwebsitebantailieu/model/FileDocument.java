package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.File;
import java.io.Serializable;

import vn.anhkhoa.projectwebsitebantailieu.enums.FileType;

public class FileDocument implements Serializable {
    private Long fileId;
    private String fileUrl;
    private FileType fileType;
    private String docName;
    private Long docId;
    private Integer docPage;

    public FileDocument(){}
    public FileDocument(Long fileId, String fileUrl, FileType fileType, String docName, Integer docPage)
    {
        this.fileId = fileId;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.docName = docName;
        this.docPage = docPage;
    }

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

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public Integer getDocPage() {
        return docPage;
    }

    public void setDocPage(Integer docPage) {
        this.docPage = docPage;
    }
}
