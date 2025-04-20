package vn.anhkhoa.projectwebsitebantailieu.model;

import java.util.List;

public class CategoryDto {
    private Long cateId;
    private String cateName;
    private String cateDesc;
    private String cateIcon;
    private List<DocumentDto> docs;

    public CategoryDto(Long cateId, String cateName, String cateDesc, String cateIcon) {
        this.cateId = cateId;
        this.cateName = cateName;
        this.cateDesc = cateDesc;
        this.cateIcon = cateIcon;
    }

    public CategoryDto(Long cateId, String cateName, String cateIcon, List<DocumentDto> docs){
        this.cateId = cateId;
        this.cateName = cateName;
        this.cateIcon = cateIcon;
        this.docs = docs;
    }

    @Override
    public String toString() {
        return this.cateName;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cate_id) {
        this.cateId = cate_id;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cate_name) {
        this.cateName = cate_name;
    }

    public String getCateDesc() {
        return cateDesc;
    }

    public void setCateDesc(String cate_desc) {
        this.cateDesc = cate_desc;
    }

    public String getCateIcon() {
        return cateIcon;
    }

    public void setCateIcon(String cate_icon) {
        this.cateIcon = cate_icon;
    }

    public List<DocumentDto> getDocs() {
        return docs;
    }

    public void setDocs(List<DocumentDto> docs) {
        this.docs = docs;
    }
}
