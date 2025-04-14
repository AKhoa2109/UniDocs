package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import vn.anhkhoa.projectwebsitebantailieu.enums.DocumentType;

public class DocumentDto implements Serializable {
    private Long docId;
    private String docName;
    private String docImageUrl;
    private Double sellPrice;
    private Double originalPrice;
    private Integer docPage;
    private Integer view;
    private Integer download;
    private String docDesc;

    private Integer maxQuantity;
    private DocumentType type;
    private LocalDateTime createdAt;
    private Long totalSold;
    private Double avgRate;

    private Long userId;

    private Long cateId;

    private Long totalReview;

    public DocumentDto() {

    }
    public DocumentDto(Long doc_id, String doc_name) {
        this.docId = doc_id;
        this.docName = doc_name;
    }

    public DocumentDto(Long docId, Long userId, Long cateId){
        this.docId = docId;
        this.userId = userId;
        this.cateId = cateId;
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

    public DocumentDto(Long doc_id, String doc_name, String doc_image_url, Double sell_price, Long totalSold) {
        this.docId = doc_id;
        this.docName = doc_name;
        this.docImageUrl = doc_image_url;
        this.sellPrice = sell_price;
        this.totalSold = totalSold;
    }

    public DocumentDto(Long doc_id, String doc_name, String doc_image_url, Double sell_price,
                       Double originalPrice, Integer docPage, Integer view, Integer maxQuantity,Integer download,
                       String docDesc, DocumentType type, LocalDateTime createdAt, Long totalSold, Double avgRate, Long userId, Long cateId, Long totalReview) {
        this.docId = doc_id;
        this.docName = doc_name;
        this.docImageUrl = doc_image_url;
        this.sellPrice = sell_price;
        this.originalPrice = originalPrice;
        this.docPage = docPage;
        this.view = view;
        this.maxQuantity = maxQuantity;
        this.download = download;
        this.docDesc = docDesc;
        this.type = type;
        this.createdAt = createdAt;
        this.totalSold = totalSold;
        this.avgRate = avgRate;
        this.userId = userId;
        this.cateId = cateId;
        this.totalReview = totalReview;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long doc_id) {
        this.docId = doc_id;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String doc_name) {
        this.docName = doc_name;
    }

    public String getDocImageUrl() {
        return docImageUrl;
    }

    public void setDocImageUrl(String doc_image_url) {
        this.docImageUrl = doc_image_url;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sell_price) {
        this.sellPrice = sell_price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getDocPage() {
        return docPage;
    }

    public void setDocPage(Integer docPage) {
        this.docPage = docPage;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Long totalSold) {
        this.totalSold = totalSold;
    }

    public Double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(Double avgRate) {
        this.avgRate = avgRate;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
    }

    public Long getTotalReview() {
        return totalReview;
    }

    public void setTotalReview(Long totalReview) {
        this.totalReview = totalReview;
    }
}