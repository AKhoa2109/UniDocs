package vn.anhkhoa.projectwebsitebantailieu.model;

public class DocumentDto {
    private Long docId;
    private String docName;
    private String docImageUrl;
    private Double sellPrice;

    public DocumentDto() {

    }
    public DocumentDto(Long doc_id, String doc_name) {
        this.docId = doc_id;
        this.docName = doc_name;
    }

    public DocumentDto(Long doc_id, String doc_name, String doc_image_url) {
        this.docId = doc_id;
        this.docName = doc_name;
        this.docImageUrl = doc_image_url;
    }
    public DocumentDto(Long doc_id, String doc_name, String doc_image_url, Double sell_price) {
        this.docId = doc_id;
        this.docName = doc_name;
        this.docImageUrl = doc_image_url;
        this.sellPrice = sell_price;
    }


    public Long getDoc_id() {
        return docId;
    }

    public void setDoc_id(Long doc_id) {
        this.docId = doc_id;
    }

    public String getDoc_name() {
        return docName;
    }

    public void setDoc_name(String doc_name) {
        this.docName = doc_name;
    }

    public String getDoc_image_url() {
        return docImageUrl;
    }

    public void setDoc_image_url(String doc_image_url) {
        this.docImageUrl = doc_image_url;
    }

    public Double getSell_price() {
        return sellPrice;
    }

    public void setSell_price(Double sell_price) {
        this.sellPrice = sell_price;
    }
}