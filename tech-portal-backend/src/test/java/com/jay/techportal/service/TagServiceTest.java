package com.jay.techportal.service;

import com.jay.techportal.dto.TagDTO;
import com.jay.techportal.entity.Tag;
import com.jay.techportal.exception.BusinessException;
import com.jay.techportal.repository.TagRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * TagService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TagService 测试")
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag testTag;
    private TagDTO testTagDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("Java");
        testTag.setSlug("java");
        testTag.setColor("#3498db");
        testTag.setDescription("Java相关技术文章");
        testTag.setCreatedAt(LocalDateTime.now());

        testTagDTO = new TagDTO();
        testTagDTO.setId(1L);
        testTagDTO.setName("Java");
        testTagDTO.setSlug("java");
        testTagDTO.setColor("#3498db");
        testTagDTO.setDescription("Java相关技术文章");
    }

    @Nested
    @DisplayName("获取所有标签测试")
    class GetAllTagsTests {

        @Test
        @DisplayName("成功获取所有标签")
        void getAllTags_Success() {
            // Given
            Tag tag2 = new Tag();
            tag2.setId(2L);
            tag2.setName("Spring");
            tag2.setSlug("spring");
            tag2.setColor("#2ecc71");

            when(tagRepository.findAll()).thenReturn(Arrays.asList(testTag, tag2));

            // When
            List<TagDTO> result = tagService.getAllTags();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Java");
            assertThat(result.get(1).getName()).isEqualTo("Spring");
            verify(tagRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("获取空标签列表")
        void getAllTags_EmptyList() {
            // Given
            when(tagRepository.findAll()).thenReturn(Arrays.asList());

            // When
            List<TagDTO> result = tagService.getAllTags();

            // Then
            assertThat(result).isEmpty();
            verify(tagRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("根据ID获取标签测试")
    class GetTagByIdTests {

        @Test
        @DisplayName("成功根据ID获取标签")
        void getTagById_Success() {
            // Given
            when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));

            // When
            TagDTO result = tagService.getTagById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Java");
            verify(tagRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("根据ID获取标签 - 标签不存在")
        void getTagById_NotFound() {
            // Given
            when(tagRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> tagService.getTagById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("标签不存在");
        }
    }

    @Nested
    @DisplayName("根据Slug获取标签测试")
    class GetTagBySlugTests {

        @Test
        @DisplayName("成功根据Slug获取标签")
        void getTagBySlug_Success() {
            // Given
            when(tagRepository.findBySlug("java")).thenReturn(Optional.of(testTag));

            // When
            TagDTO result = tagService.getTagBySlug("java");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getSlug()).isEqualTo("java");
            verify(tagRepository, times(1)).findBySlug("java");
        }

        @Test
        @DisplayName("根据Slug获取标签 - 标签不存在")
        void getTagBySlug_NotFound() {
            // Given
            when(tagRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> tagService.getTagBySlug("nonexistent"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("标签不存在");
        }
    }

    @Nested
    @DisplayName("创建标签测试")
    class CreateTagTests {

        @Test
        @DisplayName("成功创建标签")
        void createTag_Success() {
            // Given
            TagDTO newTagDTO = new TagDTO();
            newTagDTO.setName("Python");
            newTagDTO.setSlug("python");
            newTagDTO.setColor("#f39c12");
            newTagDTO.setDescription("Python相关技术");

            Tag savedTag = new Tag();
            savedTag.setId(2L);
            savedTag.setName("Python");
            savedTag.setSlug("python");
            savedTag.setColor("#f39c12");
            savedTag.setDescription("Python相关技术");

            when(tagRepository.existsByName("Python")).thenReturn(false);
            when(tagRepository.existsBySlug("python")).thenReturn(false);
            when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

            // When
            TagDTO result = tagService.createTag(newTagDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Python");
            assertThat(result.getSlug()).isEqualTo("python");
            verify(tagRepository, times(1)).save(any(Tag.class));
        }

        @Test
        @DisplayName("创建标签 - 名称已存在")
        void createTag_NameExists() {
            // Given
            TagDTO newTagDTO = new TagDTO();
            newTagDTO.setName("Java");
            newTagDTO.setSlug("java-new");

            when(tagRepository.existsByName("Java")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> tagService.createTag(newTagDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("标签名称已存在");

            verify(tagRepository, never()).save(any(Tag.class));
        }

        @Test
        @DisplayName("创建标签 - Slug已存在")
        void createTag_SlugExists() {
            // Given
            TagDTO newTagDTO = new TagDTO();
            newTagDTO.setName("Java2");
            newTagDTO.setSlug("java");

            when(tagRepository.existsByName("Java2")).thenReturn(false);
            when(tagRepository.existsBySlug("java")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> tagService.createTag(newTagDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("标签 slug 已存在");

            verify(tagRepository, never()).save(any(Tag.class));
        }

        @Test
        @DisplayName("创建标签 - 自动生成Slug")
        void createTag_AutoGenerateSlug() {
            // Given
            TagDTO newTagDTO = new TagDTO();
            newTagDTO.setName("Spring Boot");
            newTagDTO.setColor("#2ecc71");
            // slug 为 null，应该自动生成

            Tag savedTag = new Tag();
            savedTag.setId(3L);
            savedTag.setName("Spring Boot");
            savedTag.setSlug("spring-boot");
            savedTag.setColor("#2ecc71");

            when(tagRepository.existsByName("Spring Boot")).thenReturn(false);
            when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

            // When
            TagDTO result = tagService.createTag(newTagDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Spring Boot");
            verify(tagRepository, times(1)).save(any(Tag.class));
        }
    }

    @Nested
    @DisplayName("更新标签测试")
    class UpdateTagTests {

        @Test
        @DisplayName("成功更新标签")
        void updateTag_Success() {
            // Given
            TagDTO updateDTO = new TagDTO();
            updateDTO.setName("Java Updated");
            updateDTO.setColor("#e74c3c");

            Tag updatedTag = new Tag();
            updatedTag.setId(1L);
            updatedTag.setName("Java Updated");
            updatedTag.setSlug("java");
            updatedTag.setColor("#e74c3c");

            when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
            when(tagRepository.existsByName("Java Updated")).thenReturn(false);
            when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

            // When
            TagDTO result = tagService.updateTag(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Java Updated");
            verify(tagRepository, times(1)).save(any(Tag.class));
        }

        @Test
        @DisplayName("更新标签 - 标签不存在")
        void updateTag_NotFound() {
            // Given
            TagDTO updateDTO = new TagDTO();
            updateDTO.setName("Updated Name");

            when(tagRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> tagService.updateTag(999L, updateDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("标签不存在");
        }

        @Test
        @DisplayName("更新标签 - 名称与其他标签冲突")
        void updateTag_NameConflict() {
            // Given
            TagDTO updateDTO = new TagDTO();
            updateDTO.setName("Spring");

            when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
            when(tagRepository.existsByName("Spring")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> tagService.updateTag(1L, updateDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("标签名称已存在");
        }
    }

    @Nested
    @DisplayName("删除标签测试")
    class DeleteTagTests {

        @Test
        @DisplayName("成功删除标签")
        void deleteTag_Success() {
            // Given
            when(tagRepository.existsById(1L)).thenReturn(true);
            doNothing().when(tagRepository).deleteById(1L);

            // When
            tagService.deleteTag(1L);

            // Then
            verify(tagRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("删除标签 - 标签不存在")
        void deleteTag_NotFound() {
            // Given
            when(tagRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> tagService.deleteTag(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("标签不存在");

            verify(tagRepository, never()).deleteById(anyLong());
        }
    }
}
