package vn.anhkhoa.projectwebsitebantailieu.model;

public class CategoryDto {
    private Long cate_id;
    private String cate_name;
    private String cate_desc;
    private String cate_icon;

    public CategoryDto(Long cate_id, String cate_name, String cate_desc, String cate_icon) {
        this.cate_id = cate_id;
        this.cate_name = cate_name;
        this.cate_desc = cate_desc;
        this.cate_icon = cate_icon;
    }

    public Long getCate_id() {
        return cate_id;
    }

    public void setCate_id(Long cate_id) {
        this.cate_id = cate_id;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public String getCate_desc() {
        return cate_desc;
    }

    public void setCate_desc(String cate_desc) {
        this.cate_desc = cate_desc;
    }

    public String getCate_icon() {
        return cate_icon;
    }

    public void setCate_icon(String cate_icon) {
        this.cate_icon = cate_icon;
    }
}
