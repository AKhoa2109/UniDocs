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
}
