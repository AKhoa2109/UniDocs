package vn.anhkhoa.projectwebsitebantailieu.model;

public class CategoryDto {
    private Long cateId;
    private String cateName;
    private String cateDesc;
    private String cateIcon;

    public CategoryDto(Long cate_id, String cate_name, String cate_desc, String cate_icon) {
        this.cateId = cate_id;
        this.cateName = cate_name;
        this.cateDesc = cate_desc;
        this.cateIcon = cate_icon;
    }

    public Long getCate_id() {
        return cateId;
    }

    public void setCate_id(Long cate_id) {
        this.cateId = cate_id;
    }

    public String getCate_name() {
        return cateName;
    }

    public void setCate_name(String cate_name) {
        this.cateName = cate_name;
    }

    public String getCate_desc() {
        return cateDesc;
    }

    public void setCate_desc(String cate_desc) {
        this.cateDesc = cate_desc;
    }

    public String getCate_icon() {
        return cateIcon;
    }

    public void setCate_icon(String cate_icon) {
        this.cateIcon = cate_icon;
    }
}
