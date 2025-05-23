package vn.anhkhoa.projectwebsitebantailieu.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import vn.anhkhoa.projectwebsitebantailieu.enums.ChatStatus;
import vn.anhkhoa.projectwebsitebantailieu.enums.ChatType;

public class ChatLineDto {
    private Long chatLineId;
    private Long chatLineParentId;
    private String content;//
    private ChatStatus chatStatus;//

    private ChatType chatType;

    private LocalDateTime sendAt;
    private Long userId;//
    private Long conversationId;//
    @Nullable
    private List<FileChatLine> fileChatLines;
    private List<Seen> seens;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    // Constructor rỗng
    public ChatLineDto() {}

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    // Getter và Setter
    public Long getChatLineId() {
        return chatLineId;
    }

    public void setChatLineId(Long chatLineId) {
        this.chatLineId = chatLineId;
    }

    public Long getChatLineParentId() {
        return chatLineParentId;
    }

    public void setChatLineParentId(Long chatLineParentId) {
        this.chatLineParentId = chatLineParentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(ChatStatus chatStatus) {
        this.chatStatus = chatStatus;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public List<FileChatLine> getFileChatLines() {
        return fileChatLines;
    }

    public void setFileChatLines(List<FileChatLine> fileChatLines) {
        this.fileChatLines = fileChatLines;
    }

    public List<Seen> getSeens() {
        return seens;
    }

    public void setSeens(List<Seen> seens) {
        this.seens = seens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatLineDto chatLineDto = (ChatLineDto) o;
        // Chỉ so sánh các trường ảnh hưởng đến UI
        return Objects.equals(chatLineId, chatLineDto.chatLineId)
                && Objects.equals(content, chatLineDto.content)
                && chatStatus == chatLineDto.chatStatus;
    }


    @Override
    public int hashCode() {
        // Chỉ hash các trường được dùng trong equals()
        return Objects.hash(
                chatLineId,
                content,
                chatStatus,
                sendAt
        );
    }
}
