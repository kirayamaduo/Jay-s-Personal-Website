package com.jay.techportal.controller;

import com.jay.techportal.common.Result;
import com.jay.techportal.dto.CreateMessageRequest;
import com.jay.techportal.dto.MessageDTO;
import com.jay.techportal.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息相关接口（联系表单）
 */
@Tag(name = "联系消息", description = "联系表单和消息管理接口")
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    // ==================== 公开 API ====================

    /**
     * 提交联系表单
     */
    @Operation(summary = "提交联系表单", description = "访客提交联系消息")
    @PostMapping("/api/messages")
    public Result<MessageDTO> createMessage(
            @Valid @RequestBody CreateMessageRequest request,
            HttpServletRequest httpRequest) {
        MessageDTO message = messageService.createMessage(request, httpRequest);
        return Result.success("消息发送成功，我们会尽快回复您", message);
    }

    // ==================== 管理接口（后续会加权限控制） ====================

    /**
     * 获取所有消息
     */
    @Operation(summary = "获取所有消息", description = "获取所有联系消息列表")
    @GetMapping("/api/admin/messages")
    public Result<List<MessageDTO>> getAllMessages() {
        List<MessageDTO> messages = messageService.getAllMessages();
        return Result.success(messages);
    }

    /**
     * 获取未读消息
     */
    @Operation(summary = "获取未读消息", description = "获取所有未读消息列表")
    @GetMapping("/api/admin/messages/unread")
    public Result<List<MessageDTO>> getUnreadMessages() {
        List<MessageDTO> messages = messageService.getUnreadMessages();
        return Result.success(messages);
    }

    /**
     * 获取未读消息数量
     */
    @Operation(summary = "获取未读消息数量", description = "获取未读消息的数量")
    @GetMapping("/api/admin/messages/unread/count")
    public Result<Long> getUnreadCount() {
        long count = messageService.getUnreadCount();
        return Result.success(count);
    }

    /**
     * 获取消息详情
     */
    @Operation(summary = "获取消息详情", description = "根据ID获取消息详细信息")
    @GetMapping("/api/admin/messages/{id}")
    public Result<MessageDTO> getMessageById(
            @Parameter(description = "消息ID", required = true)
            @PathVariable Long id) {
        MessageDTO message = messageService.getMessageById(id);
        return Result.success(message);
    }

    /**
     * 标记消息为已读
     */
    @Operation(summary = "标记为已读", description = "将消息标记为已读状态")
    @PutMapping("/api/admin/messages/{id}/read")
    public Result<MessageDTO> markAsRead(
            @Parameter(description = "消息ID", required = true)
            @PathVariable Long id) {
        MessageDTO message = messageService.markAsRead(id);
        return Result.success("已标记为已读", message);
    }

    /**
     * 批量标记为已读
     */
    @Operation(summary = "全部标记为已读", description = "将所有未读消息标记为已读")
    @PutMapping("/api/admin/messages/read-all")
    public Result<Void> markAllAsRead() {
        messageService.markAllAsRead();
        return Result.success("已全部标记为已读", null);
    }

    /**
     * 标记消息为已回复
     */
    @Operation(summary = "标记为已回复", description = "将消息标记为已回复状态")
    @PutMapping("/api/admin/messages/{id}/replied")
    public Result<MessageDTO> markAsReplied(
            @Parameter(description = "消息ID", required = true)
            @PathVariable Long id) {
        MessageDTO message = messageService.markAsReplied(id);
        return Result.success("已标记为已回复", message);
    }

    /**
     * 删除消息
     */
    @Operation(summary = "删除消息", description = "删除指定消息")
    @DeleteMapping("/api/admin/messages/{id}")
    public Result<Void> deleteMessage(
            @Parameter(description = "消息ID", required = true)
            @PathVariable Long id) {
        messageService.deleteMessage(id);
        return Result.success("消息删除成功", null);
    }

    /**
     * 搜索消息
     */
    @Operation(summary = "搜索消息", description = "根据关键词搜索消息")
    @GetMapping("/api/admin/messages/search")
    public Result<List<MessageDTO>> searchMessages(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword) {
        List<MessageDTO> messages = messageService.searchMessages(keyword);
        return Result.success(messages);
    }
}
