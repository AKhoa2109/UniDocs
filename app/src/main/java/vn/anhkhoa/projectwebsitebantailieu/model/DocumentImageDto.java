package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.Serializable;

public class DocumentImageDto implements Serializable {

    private Long imageId;
    private String docImageUrl;
    private Long docId;

    public DocumentImageDto() {
    }

    public DocumentImageDto(Long imageId, String docImageUrl) {
        this.imageId = imageId;
        this.docImageUrl = docImageUrl;
    }

    public DocumentImageDto(Long imageId, String docImageUrl, Long docId) {
        this.imageId = imageId;
        this.docImageUrl = docImageUrl;
        this.docId = docId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getDocImageUrl() {
        return docImageUrl;
    }

    public void setDocImageUrl(String docImageUrl) {
        this.docImageUrl = docImageUrl;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }
}
