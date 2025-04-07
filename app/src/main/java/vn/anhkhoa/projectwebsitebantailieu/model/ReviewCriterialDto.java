package vn.anhkhoa.projectwebsitebantailieu.model;

import java.io.Serializable;

public class ReviewCriterialDto implements Serializable {
    private Long id;
    private String content;
    private String name;

    public ReviewCriterialDto(Long id, String content, String name) {
        this.id = id;
        this.content = content;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
