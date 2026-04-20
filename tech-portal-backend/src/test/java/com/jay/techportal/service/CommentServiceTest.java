package com.jay.techportal.service;

import com.jay.techportal.dto.CommentDTO;
import com.jay.techportal.dto.CreateCommentRequest;
import com.jay.techportal.entity.Comment;
import com.jay.techportal.entity.Post;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.CommentRepository;
import com.jay.techportal.repository.PostRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * CommentService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 测试")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private CommentService commentService;

    private Post testPost;
    private Comment testComment;
    private CreateCommentRequest createRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试文章
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("测试文章");
        testPost.setSlug("test-post");
        testPost.setIsPublished(true);

        // 初始化测试评论
        testComment = new Comment();
        testComment.setId(1L);
        testComment.setPost(testPost);
        testComment.setNickname("测试用户");
        testComment.setEmail("test@example.com");
        testComment.setContent("这是一条测试评论");
        testComment.setIsApproved(true);
        testComment.setIsSpam(false);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setReplies(new ArrayList<>());

        // 初始化创建请求
        createRequest = new CreateCommentRequest();
        createRequest.setNickname("新用户");
        createRequest.setEmail("newuser@example.com");
        createRequest.setContent("新的评论内容");
    }

    @Nested
    @DisplayName("获取评论列表测试")
    class GetCommentsTests {

        @Test
        @DisplayName("获取文章评论列表")
        void getCommentsByPostId_Success() {
            // Given
            when(postRepository.existsById(1L)).thenReturn(true);
            when(commentRepository.findTopLevelCommentsByPostId(1L))
                    .thenReturn(Arrays.asList(testComment));

            // When
            List<CommentDTO> result = commentService.getCommentsByPostId(1L);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNickname()).isEqualTo("测试用户");
        }

        @Test
        @DisplayName("获取评论列表 - 文章不存在")
        void getCommentsByPostId_PostNotFound() {
            // Given
            when(postRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> commentService.getCommentsByPostId(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("文章不存在");
        }

        @Test
        @DisplayName("获取评论数量")
        void getCommentCount_Success() {
            // Given
            when(commentRepository.countApprovedByPostId(1L)).thenReturn(10L);

            // When
            long count = commentService.getCommentCount(1L);

            // Then
            assertThat(count).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("创建评论测试")
    class CreateCommentTests {

        @Test
        @DisplayName("成功创建评论")
        void createComment_Success() {
            // Given
            when(postRepository.findPublishedById(1L)).thenReturn(Optional.of(testPost));
            when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
            when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
            when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
            when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

            Comment savedComment = new Comment();
            savedComment.setId(2L);
            savedComment.setPost(testPost);
            savedComment.setNickname("新用户");
            savedComment.setEmail("newuser@example.com");
            savedComment.setContent("新的评论内容");
            savedComment.setIsApproved(true);
            savedComment.setIsSpam(false);

            when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

            // When
            CommentDTO result = commentService.createComment(1L, createRequest, httpServletRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNickname()).isEqualTo("新用户");
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("创建评论 - 文章不存在")
        void createComment_PostNotFound() {
            // Given
            when(postRepository.findPublishedById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> commentService.createComment(999L, createRequest, httpServletRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("文章不存在");
        }

        @Test
        @DisplayName("创建回复评论")
        void createComment_WithParent() {
            // Given
            createRequest.setParentId(1L);

            when(postRepository.findPublishedById(1L)).thenReturn(Optional.of(testPost));
            when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
            when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
            when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
            when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
            when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

            Comment replyComment = new Comment();
            replyComment.setId(2L);
            replyComment.setPost(testPost);
            replyComment.setNickname("新用户");
            replyComment.setEmail("newuser@example.com");
            replyComment.setContent("新的评论内容");
            replyComment.setParent(testComment);
            replyComment.setReplyToNickname("测试用户");
            replyComment.setIsApproved(true);

            when(commentRepository.save(any(Comment.class))).thenReturn(replyComment);

            // When
            CommentDTO result = commentService.createComment(1L, createRequest, httpServletRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getReplyToNickname()).isEqualTo("测试用户");
        }

        @Test
        @DisplayName("创建回复 - 父评论不存在")
        void createComment_ParentNotFound() {
            // Given
            createRequest.setParentId(999L);

            when(postRepository.findPublishedById(1L)).thenReturn(Optional.of(testPost));
            when(commentRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> commentService.createComment(1L, createRequest, httpServletRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("被回复的评论不存在");
        }

        @Test
        @DisplayName("创建回复 - 父评论不属于同一文章")
        void createComment_ParentFromDifferentPost() {
            // Given
            createRequest.setParentId(1L);

            Post otherPost = new Post();
            otherPost.setId(2L);
            testComment.setPost(otherPost);

            when(postRepository.findPublishedById(1L)).thenReturn(Optional.of(testPost));
            when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

            // When & Then
            assertThatThrownBy(() -> commentService.createComment(1L, createRequest, httpServletRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("无效的父评论");
        }
    }

    @Nested
    @DisplayName("审核评论测试")
    class ApproveCommentTests {

        @Test
        @DisplayName("成功审核评论")
        void approveComment_Success() {
            // Given
            testComment.setIsApproved(false);
            
            Comment approvedComment = new Comment();
            approvedComment.setId(1L);
            approvedComment.setNickname("测试用户");
            approvedComment.setIsApproved(true);

            when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(approvedComment);

            // When
            CommentDTO result = commentService.approveComment(1L);

            // Then
            assertThat(result).isNotNull();
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("审核评论 - 评论不存在")
        void approveComment_NotFound() {
            // Given
            when(commentRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> commentService.approveComment(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("评论不存在");
        }
    }

    @Nested
    @DisplayName("标记垃圾评论测试")
    class MarkAsSpamTests {

        @Test
        @DisplayName("成功标记为垃圾评论")
        void markAsSpam_Success() {
            // Given
            when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

            // When
            commentService.markAsSpam(1L);

            // Then
            verify(commentRepository, times(1)).save(any(Comment.class));
            assertThat(testComment.getIsSpam()).isTrue();
        }

        @Test
        @DisplayName("标记垃圾评论 - 评论不存在")
        void markAsSpam_NotFound() {
            // Given
            when(commentRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> commentService.markAsSpam(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("评论不存在");
        }
    }

    @Nested
    @DisplayName("删除评论测试")
    class DeleteCommentTests {

        @Test
        @DisplayName("成功删除评论")
        void deleteComment_Success() {
            // Given
            when(commentRepository.existsById(1L)).thenReturn(true);
            doNothing().when(commentRepository).deleteById(1L);

            // When
            commentService.deleteComment(1L);

            // Then
            verify(commentRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("删除评论 - 评论不存在")
        void deleteComment_NotFound() {
            // Given
            when(commentRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> commentService.deleteComment(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("评论不存在");

            verify(commentRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("获取待审核评论测试")
    class GetPendingCommentsTests {

        @Test
        @DisplayName("获取待审核评论列表")
        void getPendingComments_Success() {
            // Given
            Comment pendingComment = new Comment();
            pendingComment.setId(2L);
            pendingComment.setNickname("待审核用户");
            pendingComment.setIsApproved(false);

            when(commentRepository.findPendingComments())
                    .thenReturn(Arrays.asList(pendingComment));

            // When
            List<CommentDTO> result = commentService.getPendingComments();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNickname()).isEqualTo("待审核用户");
        }

        @Test
        @DisplayName("获取待审核评论 - 无待审核")
        void getPendingComments_Empty() {
            // Given
            when(commentRepository.findPendingComments()).thenReturn(Arrays.asList());

            // When
            List<CommentDTO> result = commentService.getPendingComments();

            // Then
            assertThat(result).isEmpty();
        }
    }
}
