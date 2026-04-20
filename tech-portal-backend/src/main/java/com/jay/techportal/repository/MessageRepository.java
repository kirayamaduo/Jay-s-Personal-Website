package com.jay.techportal.repository;

import com.jay.techportal.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 获取所有消息，按创建时间倒序
     */
    List<Message> findAllByOrderByCreatedAtDesc();

    /**
     * 分页获取消息
     */
    Page<Message> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 获取未读消息
     */
    List<Message> findByIsReadFalseOrderByCreatedAtDesc();

    /**
     * 统计未读消息数量
     */
    long countByIsReadFalse();

    /**
     * 获取未回复消息
     */
    List<Message> findByIsRepliedFalseOrderByCreatedAtDesc();

    /**
     * 根据邮箱查找消息
     */
    List<Message> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * 搜索消息（按姓名、邮箱、主题、内容）
     */
    @Query("SELECT m FROM Message m " +
            "WHERE m.name LIKE %:keyword% " +
            "OR m.email LIKE %:keyword% " +
            "OR m.subject LIKE %:keyword% " +
            "OR m.content LIKE %:keyword% " +
            "ORDER BY m.createdAt DESC")
    List<Message> searchMessages(String keyword);
}
