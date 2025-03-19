package vn.anhkhoa.projectwebsitebantailieu.model;

public class DocumentDto {
    private Long doc_id;
    private String doc_name;
    private String doc_image_url;
    private Double sell_price;

    public DocumentDto() {

    }
    public DocumentDto(Long doc_id, String doc_name) {
        this.doc_id = doc_id;
        this.doc_name = doc_name;
    }

    public DocumentDto(Long doc_id, String doc_name, String doc_image_url) {
        this.doc_id = doc_id;
        this.doc_name = doc_name;
        this.doc_image_url = doc_image_url;
    }
    public DocumentDto(Long doc_id, String doc_name, String doc_image_url, Double sell_price) {
        this.doc_id = doc_id;
        this.doc_name = doc_name;
        this.doc_image_url = doc_image_url;
        this.sell_price = sell_price;
    }


    public Long getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Long doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDoc_image_url() {
        return doc_image_url;
    }

    public void setDoc_image_url(String doc_image_url) {
        this.doc_image_url = doc_image_url;
    }

    public Double getSell_price() {
        return sell_price;
    }

    public void setSell_price(Double sell_price) {
        this.sell_price = sell_price;
    }
}