package com.jay.techportal.service;

import com.jay.techportal.common.ResultCode;
import com.jay.techportal.dto.CreateMessageRequest;
import com.jay.techportal.dto.MessageDTO;
import com.jay.techportal.entity.Message;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.MessageRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // ==================== 公开 API ====================

    /**
     * 提交联系表单
     */
    @Transactional
    public MessageDTO createMessage(CreateMessageRequest request, HttpServletRequest httpRequest) {
        Message message = new Message();
        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setSubject(request.getSubject());
        message.setContent(request.getContent());
        message.setIsRead(false);
        message.setIsReplied(false);

        // 记录 IP 和 User-Agent
        if (httpRequest != null) {
            message.setIpAddress(getClientIp(httpRequest));
            message.setUserAgent(truncate(httpRequest.getHeader("User-Agent"), 500));
        }

        Message savedMessage = messageRepository.save(message);
        return MessageDTO.fromEntity(savedMessage);
    }

    // ==================== 管理后台 API ====================

    /**
     * 获取所有消息
     */
    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取未读消息
     */
    public List<MessageDTO> getUnreadMessages() {
        return messageRepository.findByIsReadFalseOrderByCreatedAtDesc().stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 获取未读消息数量
     */
    public long getUnreadCount() {
        return messageRepository.countByIsReadFalse();
    }

    /**
     * 获取消息详情
     */
    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "消息不存在"));
        return MessageDTO.fromEntity(message);
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public MessageDTO markAsRead(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "消息不存在"));
        message.setIsRead(true);
        Message savedMessage = messageRepository.save(message);
        return MessageDTO.fromEntity(savedMessage);
    }

    /**
     * 批量标记为已读
     */
    @Transactional
    public void markAllAsRead() {
        List<Message> unreadMessages = messageRepository.findByIsReadFalseOrderByCreatedAtDesc();
        unreadMessages.forEach(message -> message.setIsRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    /**
     * 标记消息为已回复
     */
    @Transactional
    public MessageDTO markAsReplied(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "消息不存在"));
        message.setIsReplied(true);
        message.setRepliedAt(LocalDateTime.now());
        message.setIsRead(true); // 回复时自动标记为已读
        Message savedMessage = messageRepository.save(message);
        return MessageDTO.fromEntity(savedMessage);
    }

    /**
     * 删除消息
     */
    @Transactional
    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "消息不存在");
        }
        messageRepository.deleteById(id);
    }

    /**
     * 搜索消息
     */
    public List<MessageDTO> searchMessages(String keyword) {
        return messageRepository.searchMessages(keyword).stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== 工具方法 ====================

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 截断字符串
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
