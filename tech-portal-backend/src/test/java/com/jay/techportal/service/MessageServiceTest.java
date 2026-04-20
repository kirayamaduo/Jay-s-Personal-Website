package com.jay.techportal.service;

import com.jay.techportal.dto.CreateMessageRequest;
import com.jay.techportal.dto.MessageDTO;
import com.jay.techportal.entity.Message;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.MessageRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * MessageService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 测试")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private MessageService messageService;

    private Message testMessage;
    private CreateMessageRequest createRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试消息
        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setName("张三");
        testMessage.setEmail("zhangsan@example.com");
        testMessage.setSubject("测试主题");
        testMessage.setContent("这是一条测试消息");
        testMessage.setIsRead(false);
        testMessage.setIsReplied(false);
        testMessage.setIpAddress("127.0.0.1");
        testMessage.setCreatedAt(LocalDateTime.now());

        // 初始化创建请求
        createRequest = new CreateMessageRequest();
        createRequest.setName("张三");
        createRequest.setEmail("zhangsan@example.com");
        createRequest.setSubject("测试主题");
        createRequest.setContent("这是一条测试消息");
    }

    @Nested
    @DisplayName("创建消息测试")
    class CreateMessageTests {

        @Test
        @DisplayName("成功创建消息")
        void createMessage_Success() {
            // Given
            when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
            when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
            when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
            when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
            when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

            // When
            MessageDTO result = messageService.createMessage(createRequest, httpServletRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("张三");
            assertThat(result.getEmail()).isEqualTo("zhangsan@example.com");
            verify(messageRepository, times(1)).save(any(Message.class));
        }

        @Test
        @DisplayName("创建消息 - 从X-Forwarded-For获取IP")
        void createMessage_WithXForwardedFor() {
            // Given
            when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");
            when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
            when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

            // When
            MessageDTO result = messageService.createMessage(createRequest, httpServletRequest);

            // Then
            assertThat(result).isNotNull();
            verify(messageRepository, times(1)).save(any(Message.class));
        }

        @Test
        @DisplayName("创建消息 - 无HttpRequest")
        void createMessage_WithoutHttpRequest() {
            // Given
            when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

            // When
            MessageDTO result = messageService.createMessage(createRequest, null);

            // Then
            assertThat(result).isNotNull();
            verify(messageRepository, times(1)).save(any(Message.class));
        }
    }

    @Nested
    @DisplayName("获取消息列表测试")
    class GetMessagesTests {

        @Test
        @DisplayName("获取所有消息")
        void getAllMessages_Success() {
            // Given
            Message message2 = new Message();
            message2.setId(2L);
            message2.setName("李四");
            message2.setEmail("lisi@example.com");
            message2.setContent("另一条消息");
            message2.setIsRead(true);
            message2.setIsReplied(false);

            when(messageRepository.findAllByOrderByCreatedAtDesc())
                    .thenReturn(Arrays.asList(testMessage, message2));

            // When
            List<MessageDTO> result = messageService.getAllMessages();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("张三");
            assertThat(result.get(1).getName()).isEqualTo("李四");
        }

        @Test
        @DisplayName("获取未读消息")
        void getUnreadMessages_Success() {
            // Given
            when(messageRepository.findByIsReadFalseOrderByCreatedAtDesc())
                    .thenReturn(Arrays.asList(testMessage));

            // When
            List<MessageDTO> result = messageService.getUnreadMessages();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIsRead()).isFalse();
        }

        @Test
        @DisplayName("获取未读消息数量")
        void getUnreadCount_Success() {
            // Given
            when(messageRepository.countByIsReadFalse()).thenReturn(5L);

            // When
            long result = messageService.getUnreadCount();

            // Then
            assertThat(result).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("获取单条消息测试")
    class GetMessageByIdTests {

        @Test
        @DisplayName("成功获取消息详情")
        void getMessageById_Success() {
            // Given
            when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));

            // When
            MessageDTO result = messageService.getMessageById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("张三");
        }

        @Test
        @DisplayName("获取消息详情 - 消息不存在")
        void getMessageById_NotFound() {
            // Given
            when(messageRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.getMessageById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("消息不存在");
        }
    }

    @Nested
    @DisplayName("标记消息状态测试")
    class MarkMessageTests {

        @Test
        @DisplayName("标记消息为已读")
        void markAsRead_Success() {
            // Given
            when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));
            
            Message readMessage = new Message();
            readMessage.setId(1L);
            readMessage.setName("张三");
            readMessage.setEmail("zhangsan@example.com");
            readMessage.setIsRead(true);
            readMessage.setIsReplied(false);
            
            when(messageRepository.save(any(Message.class))).thenReturn(readMessage);

            // When
            MessageDTO result = messageService.markAsRead(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIsRead()).isTrue();
            verify(messageRepository, times(1)).save(any(Message.class));
        }

        @Test
        @DisplayName("标记消息为已读 - 消息不存在")
        void markAsRead_NotFound() {
            // Given
            when(messageRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.markAsRead(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("消息不存在");
        }

        @Test
        @DisplayName("批量标记为已读")
        void markAllAsRead_Success() {
            // Given
            Message unreadMessage1 = new Message();
            unreadMessage1.setId(1L);
            unreadMessage1.setIsRead(false);
            
            Message unreadMessage2 = new Message();
            unreadMessage2.setId(2L);
            unreadMessage2.setIsRead(false);

            when(messageRepository.findByIsReadFalseOrderByCreatedAtDesc())
                    .thenReturn(Arrays.asList(unreadMessage1, unreadMessage2));

            // When
            messageService.markAllAsRead();

            // Then
            verify(messageRepository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("标记消息为已回复")
        void markAsReplied_Success() {
            // Given
            when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));
            
            Message repliedMessage = new Message();
            repliedMessage.setId(1L);
            repliedMessage.setName("张三");
            repliedMessage.setEmail("zhangsan@example.com");
            repliedMessage.setIsRead(true);
            repliedMessage.setIsReplied(true);
            repliedMessage.setRepliedAt(LocalDateTime.now());
            
            when(messageRepository.save(any(Message.class))).thenReturn(repliedMessage);

            // When
            MessageDTO result = messageService.markAsReplied(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIsReplied()).isTrue();
            assertThat(result.getIsRead()).isTrue();
        }
    }

    @Nested
    @DisplayName("删除消息测试")
    class DeleteMessageTests {

        @Test
        @DisplayName("成功删除消息")
        void deleteMessage_Success() {
            // Given
            when(messageRepository.existsById(1L)).thenReturn(true);
            doNothing().when(messageRepository).deleteById(1L);

            // When
            messageService.deleteMessage(1L);

            // Then
            verify(messageRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("删除消息 - 消息不存在")
        void deleteMessage_NotFound() {
            // Given
            when(messageRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> messageService.deleteMessage(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("消息不存在");

            verify(messageRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("搜索消息测试")
    class SearchMessageTests {

        @Test
        @DisplayName("成功搜索消息")
        void searchMessages_Success() {
            // Given
            when(messageRepository.searchMessages("张三"))
                    .thenReturn(Arrays.asList(testMessage));

            // When
            List<MessageDTO> result = messageService.searchMessages("张三");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("张三");
        }

        @Test
        @DisplayName("搜索消息 - 无结果")
        void searchMessages_NoResults() {
            // Given
            when(messageRepository.searchMessages("不存在"))
                    .thenReturn(Arrays.asList());

            // When
            List<MessageDTO> result = messageService.searchMessages("不存在");

            // Then
            assertThat(result).isEmpty();
        }
    }
}
