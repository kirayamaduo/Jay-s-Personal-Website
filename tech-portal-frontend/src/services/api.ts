/**
 * API 服务层 - 与后端通信
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

/**
 * 通用 API 响应格式
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

/**
 * 标签 DTO
 */
export interface TagDTO {
  id: number;
  name: string;
  slug: string;
  color: string | null;
  description: string | null;
}

/**
 * 项目列表 DTO
 */
export interface ProjectListDTO {
  id: number;
  title: string;
  slug: string;
  description: string;
  icon: string | null;
  gradientClass: string | null;
  githubUrl: string | null;
  demoUrl: string | null;
  starsCount: number | null;
  forksCount: number | null;
  techStack: Array<{ name: string; description: string }> | null;
  isFeatured: boolean;
  displayOrder: number;
  tags: TagDTO[];
}

/**
 * 项目详情 DTO
 */
export interface ProjectDetailDTO extends ProjectListDTO {
  content: string | null;
  contributorsCount: number | null;
  lastCommitAt: string | null;
  features: string[] | null;
  galleryImages: string[] | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * 文章列表 DTO
 */
export interface PostListDTO {
  id: number;
  title: string;
  slug: string;
  excerpt: string | null;
  coverImage: string | null;
  isFeatured: boolean;
  viewCount: number;
  readingTimeMinutes: number | null;
  publishedAt: string;
  authorName: string | null;
  tags: TagDTO[];
}

/**
 * 文章详情 DTO
 */
export interface PostDetailDTO extends PostListDTO {
  content: string;
  renderedContent: string | null;
  seoTitle: string | null;
  seoDescription: string | null;
  createdAt: string;
  updatedAt: string;
  authorId: number | null;
  authorAvatar: string | null;
}

/**
 * 经历 DTO
 */
export interface ExperienceDTO {
  id: number;
  type: 'work' | 'education';
  title: string;
  company: string;
  location: string | null;
  startDate: string;
  endDate: string | null;
  description: string | null;
  achievements: string[] | null;
  displayOrder: number;
}

/**
 * 社交链接 DTO
 */
export interface SocialLinkDTO {
  id: number;
  platform: string;
  icon: string;
  label: string;
  url: string;
  username: string | null;
  displayOrder: number;
}

/**
 * 评论 DTO
 */
export interface CommentDTO {
  id: number;
  postId: number;
  parentId: number | null;
  nickname: string;
  website: string | null;
  content: string;
  replyToNickname: string | null;
  createdAt: string;
  replies: CommentDTO[];
}

/**
 * 创建评论请求
 */
export interface CreateCommentRequest {
  nickname: string;
  email?: string;
  website?: string;
  content: string;
  parentId?: number;
}

/**
 * 创建消息请求
 */
export interface CreateMessageRequest {
  name: string;
  email: string;
  subject?: string;
  content: string;
}

// ============ API 请求函数 ============

/**
 * 通用请求函数
 */
async function request<T>(endpoint: string, options?: RequestInit): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
    ...options,
  });

  if (!response.ok) {
    throw new Error(`API Error: ${response.status} ${response.statusText}`);
  }

  const result: ApiResponse<T> = await response.json();
  
  if (result.code !== 200) {
    throw new Error(result.message || 'Unknown error');
  }

  return result.data;
}

// ============ Projects API ============

export const projectsApi = {
  /** 获取所有活跃项目 */
  getAll: () => request<ProjectListDTO[]>('/projects'),
  
  /** 获取精选项目 */
  getFeatured: () => request<ProjectListDTO[]>('/projects/featured'),
  
  /** 根据 ID 获取项目详情 */
  getById: (id: number | string) => request<ProjectDetailDTO>(`/projects/${id}`),
  
  /** 根据 slug 获取项目详情 */
  getBySlug: (slug: string) => request<ProjectDetailDTO>(`/projects/slug/${slug}`),
};

// ============ Posts API ============

export const postsApi = {
  /** 获取所有已发布文章 */
  getAll: () => request<PostListDTO[]>('/posts'),
  
  /** 获取最新文章 */
  getLatest: (limit: number = 5) => request<PostListDTO[]>(`/posts/latest?limit=${limit}`),
  
  /** 获取精选文章 */
  getFeatured: () => request<PostListDTO[]>('/posts/featured'),
  
  /** 根据 ID 获取文章详情 */
  getById: (id: number | string) => request<PostDetailDTO>(`/posts/${id}`),
  
  /** 根据 slug 获取文章详情 */
  getBySlug: (slug: string) => request<PostDetailDTO>(`/posts/slug/${slug}`),
  
  /** 根据标签获取文章 */
  getByTag: (tagSlug: string) => request<PostListDTO[]>(`/posts/tag/${tagSlug}`),
};

// ============ Tags API ============

export const tagsApi = {
  /** 获取所有标签 */
  getAll: () => request<TagDTO[]>('/tags'),
  
  /** 根据 ID 获取标签 */
  getById: (id: number) => request<TagDTO>(`/tags/${id}`),
  
  /** 根据 slug 获取标签 */
  getBySlug: (slug: string) => request<TagDTO>(`/tags/slug/${slug}`),
};

// ============ Experiences API ============

export const experiencesApi = {
  /** 获取所有活跃经历 */
  getAll: () => request<ExperienceDTO[]>('/experiences'),
  
  /** 根据类型获取经历 */
  getByType: (type: 'work' | 'education') => request<ExperienceDTO[]>(`/experiences/type/${type}`),
};

// ============ Social Links API ============

export const socialLinksApi = {
  /** 获取所有活跃社交链接 */
  getAll: () => request<SocialLinkDTO[]>('/social-links'),
};

// ============ Comments API ============

export const commentsApi = {
  /** 获取文章评论 */
  getByPostId: (postId: number) => request<CommentDTO[]>(`/posts/${postId}/comments`),
  
  /** 获取文章评论数量 */
  getCountByPostId: (postId: number) => request<number>(`/posts/${postId}/comments/count`),
  
  /** 创建评论 */
  create: (postId: number, data: CreateCommentRequest) =>
    request<CommentDTO>(`/posts/${postId}/comments`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),
};

// ============ Messages API ============

export const messagesApi = {
  /** 发送联系消息 */
  send: (data: CreateMessageRequest) =>
    request<{ id: number }>('/messages', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
};

// ============ Auth Types ============

/**
 * 用户信息
 */
export interface UserInfo {
  id: number;
  username: string;
  email: string | null;
  avatarUrl: string | null;
  role: string;
}

/**
 * 认证响应
 */
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserInfo;
}

/**
 * 登录请求
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 刷新 Token 请求
 */
export interface RefreshTokenRequest {
  refreshToken: string;
}

// ============ Auth API ============

/**
 * 带认证的请求函数
 */
async function authRequest<T>(endpoint: string, options?: RequestInit): Promise<T> {
  const token = localStorage.getItem('accessToken');
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options?.headers,
  };
  
  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
  }
  
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetch(url, {
    ...options,
    headers,
  });

  if (!response.ok) {
    // 如果是 401，可能需要刷新 token
    if (response.status === 401) {
      throw new Error('UNAUTHORIZED');
    }
    throw new Error(`API Error: ${response.status} ${response.statusText}`);
  }

  const result: ApiResponse<T> = await response.json();
  
  if (result.code !== 200) {
    throw new Error(result.message || 'Unknown error');
  }

  return result.data;
}

export const authApi = {
  /** 登录 */
  login: (data: LoginRequest) =>
    request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  /** 刷新 Token */
  refresh: (data: RefreshTokenRequest) =>
    request<AuthResponse>('/auth/refresh', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  /** 登出 */
  logout: () =>
    authRequest<void>('/auth/logout', {
      method: 'POST',
    }),

  /** 获取当前用户信息 */
  getCurrentUser: () => authRequest<UserInfo>('/auth/me'),
};

// 导出 authRequest 供其他模块使用
export { authRequest };

// ============ Admin Types ============

/**
 * 消息 DTO（管理后台）
 */
export interface MessageDTO {
  id: number;
  name: string;
  email: string;
  subject: string | null;
  content: string;
  isRead: boolean;
  isReplied: boolean;
  repliedAt: string | null;
  ipAddress: string | null;
  createdAt: string;
}

/**
 * 创建/更新文章请求
 */
export interface CreatePostRequest {
  title: string;
  slug?: string;
  excerpt?: string;
  content: string;
  coverImage?: string;
  seoTitle?: string;
  seoDescription?: string;
  isFeatured?: boolean;
  isPublished?: boolean;
  tagIds?: number[];
}

/**
 * 创建/更新项目请求
 */
export interface CreateProjectRequest {
  title: string;
  slug?: string;
  description: string;
  content?: string;
  icon?: string;
  gradientClass?: string;
  githubUrl?: string;
  demoUrl?: string;
  techStack?: Array<{ name: string; description: string }>;
  features?: string[];
  galleryImages?: string[];
  isFeatured?: boolean;
  isActive?: boolean;
  displayOrder?: number;
  tagIds?: number[];
}

/**
 * 统计数据
 */
export interface DashboardStats {
  postCount: number;
  projectCount: number;
  unreadMessageCount: number;
  pendingCommentCount: number;
}

// ============ Admin API ============

export const adminApi = {
  // Dashboard
  getStats: async (): Promise<DashboardStats> => {
    // 并行获取各项统计
    const [posts, projects, unreadCount, pendingComments] = await Promise.all([
      authRequest<PostListDTO[]>('/admin/posts').catch(() => []),
      authRequest<ProjectListDTO[]>('/admin/projects').catch(() => []),
      authRequest<number>('/admin/messages/unread/count').catch(() => 0),
      authRequest<CommentDTO[]>('/admin/comments/pending').catch(() => []),
    ]);
    return {
      postCount: posts.length,
      projectCount: projects.length,
      unreadMessageCount: unreadCount,
      pendingCommentCount: pendingComments.length,
    };
  },

  // Posts
  posts: {
    getAll: () => authRequest<PostListDTO[]>('/admin/posts'),
    getById: (id: number) => authRequest<PostDetailDTO>(`/admin/posts/${id}`),
    create: (data: CreatePostRequest) =>
      authRequest<PostDetailDTO>('/admin/posts', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (id: number, data: CreatePostRequest) =>
      authRequest<PostDetailDTO>(`/admin/posts/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (id: number) =>
      authRequest<void>(`/admin/posts/${id}`, { method: 'DELETE' }),
  },

  // Projects
  projects: {
    getAll: () => authRequest<ProjectListDTO[]>('/admin/projects'),
    getById: (id: number) => authRequest<ProjectDetailDTO>(`/admin/projects/${id}`),
    create: (data: CreateProjectRequest) =>
      authRequest<ProjectDetailDTO>('/admin/projects', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (id: number, data: CreateProjectRequest) =>
      authRequest<ProjectDetailDTO>(`/admin/projects/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (id: number) =>
      authRequest<void>(`/admin/projects/${id}`, { method: 'DELETE' }),
  },

  // Messages
  messages: {
    getAll: () => authRequest<MessageDTO[]>('/admin/messages'),
    getUnread: () => authRequest<MessageDTO[]>('/admin/messages/unread'),
    getUnreadCount: () => authRequest<number>('/admin/messages/unread/count'),
    getById: (id: number) => authRequest<MessageDTO>(`/admin/messages/${id}`),
    markAsRead: (id: number) =>
      authRequest<MessageDTO>(`/admin/messages/${id}/read`, { method: 'PUT' }),
    markAllAsRead: () =>
      authRequest<void>('/admin/messages/read-all', { method: 'PUT' }),
    markAsReplied: (id: number) =>
      authRequest<MessageDTO>(`/admin/messages/${id}/replied`, { method: 'PUT' }),
    delete: (id: number) =>
      authRequest<void>(`/admin/messages/${id}`, { method: 'DELETE' }),
  },

  // Comments
  comments: {
    getPending: () => authRequest<CommentDTO[]>('/admin/comments/pending'),
    approve: (id: number) =>
      authRequest<CommentDTO>(`/admin/comments/${id}/approve`, { method: 'PUT' }),
    markAsSpam: (id: number) =>
      authRequest<void>(`/admin/comments/${id}/spam`, { method: 'PUT' }),
    delete: (id: number) =>
      authRequest<void>(`/admin/comments/${id}`, { method: 'DELETE' }),
  },

  // Tags
  tags: {
    getAll: () => authRequest<TagDTO[]>('/admin/tags'),
    create: (data: { name: string; slug?: string; color?: string; description?: string }) =>
      authRequest<TagDTO>('/admin/tags', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (id: number, data: { name: string; slug?: string; color?: string; description?: string }) =>
      authRequest<TagDTO>(`/admin/tags/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (id: number) =>
      authRequest<void>(`/admin/tags/${id}`, { method: 'DELETE' }),
  },
};
