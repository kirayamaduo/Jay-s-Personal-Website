import { useState, useEffect, useCallback } from "react"
import { projects as mockProjects, blogPosts as mockBlogPosts, personalExperiences, socialLinks as mockSocialLinks, githubStats, roles } from "../data/mockData"
import type { Project, BlogPost, Experience, SocialLink, ApiProject, ApiPost, ApiExperience, ApiSocialLink } from "../types"
import { projectsApi, postsApi, experiencesApi, socialLinksApi } from "../services/api"
import { Github, Linkedin, Twitter, Mail, Globe } from "lucide-react"

// 是否使用真实 API（设为 false 使用 mock 数据进行调试）
const USE_REAL_API = true

// ============ 数据转换函数 ============

/**
 * 将 API 项目数据转换为前端 Project 类型
 */
function transformProject(apiProject: ApiProject): Project {
    return {
        id: apiProject.id,
        title: apiProject.title,
        slug: apiProject.slug,
        description: apiProject.description,
        longDescription: apiProject.content || undefined,
        gradient: apiProject.gradientClass || "from-blue-500/20 via-cyan-500/20 to-teal-500/20",
        icon: apiProject.icon || "📦",
        tags: apiProject.tags?.map(t => t.name) || [],
        github: apiProject.githubUrl || "",
        demo: apiProject.demoUrl || undefined,
        stars: apiProject.starsCount || 0,
        featured: apiProject.isFeatured,
        forks: apiProject.forksCount || undefined,
        contributors: apiProject.contributorsCount || undefined,
        lastUpdate: apiProject.lastCommitAt ? new Date(apiProject.lastCommitAt).toISOString().split('T')[0] : undefined,
        features: apiProject.features || undefined,
        techStack: apiProject.techStack || undefined,
        screenshots: apiProject.galleryImages || undefined,
    }
}

/**
 * 将 API 文章数据转换为前端 BlogPost 类型
 */
function transformPost(apiPost: ApiPost): BlogPost {
    return {
        id: apiPost.id,
        title: apiPost.title,
        slug: apiPost.slug,
        excerpt: apiPost.excerpt || "",
        date: apiPost.publishedAt ? new Date(apiPost.publishedAt).toISOString().split('T')[0] : "",
        readTime: apiPost.readingTimeMinutes ? `${apiPost.readingTimeMinutes} min` : "5 min",
        tags: apiPost.tags?.map(t => t.name) || [],
        content: apiPost.content || apiPost.renderedContent || "",
        coverImage: apiPost.coverImage || undefined,
        authorName: apiPost.authorName || undefined,
        viewCount: apiPost.viewCount,
    }
}

/**
 * 将 API 经历数据转换为前端 Experience 类型
 */
function transformExperience(apiExp: ApiExperience): Experience {
    // 格式化日期为 "2022 - Present" 格式
    const startYear = new Date(apiExp.startDate).getFullYear()
    const endYear = apiExp.endDate ? new Date(apiExp.endDate).getFullYear() : "Present"
    const period = `${startYear} - ${endYear}`

    return {
        id: apiExp.id,
        type: apiExp.type,
        title: apiExp.title,
        company: apiExp.company,
        location: apiExp.location || "",
        period,
        description: apiExp.description || "",
        achievements: apiExp.achievements || undefined,
    }
}

/**
 * 图标名称映射到 Lucide 组件
 */
const iconMap: Record<string, typeof Github> = {
    github: Github,
    linkedin: Linkedin,
    twitter: Twitter,
    mail: Mail,
    email: Mail,
    globe: Globe,
}

/**
 * 将 API 社交链接数据转换为前端 SocialLink 类型
 */
function transformSocialLink(apiLink: ApiSocialLink): SocialLink {
    const IconComponent = iconMap[apiLink.icon?.toLowerCase()] || Globe
    return {
        icon: IconComponent,
        label: apiLink.label,
        href: apiLink.url,
        username: apiLink.username || "",
    }
}

// ============ Hooks ============

export function useProjects() {
    const [data, setData] = useState<Project[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!USE_REAL_API) {
            setData(mockProjects)
            setLoading(false)
            return
        }

        const fetchData = async () => {
            try {
                setLoading(true)
                const apiProjects = await projectsApi.getAll()
                setData(apiProjects.map(transformProject))
                setError(null)
            } catch (err) {
                console.error("Failed to fetch projects:", err)
                setError(err instanceof Error ? err.message : "Failed to fetch projects")
                // 降级到 mock 数据
                setData(mockProjects)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [])

    const getProject = useCallback((id: string | number) => {
        return data.find((p) => p.id.toString() === id.toString() || p.slug === id.toString())
    }, [data])

    const getFeaturedProjects = useCallback(() => {
        return data.filter((p) => p.featured)
    }, [data])

    return { projects: data, getProject, getFeaturedProjects, loading, error }
}

export function useProjectDetail(idOrSlug: string | number) {
    const [project, setProject] = useState<Project | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!USE_REAL_API) {
            const found = mockProjects.find(p => p.id.toString() === idOrSlug.toString())
            setProject(found || null)
            setLoading(false)
            return
        }

        const fetchData = async () => {
            try {
                setLoading(true)
                // 尝试判断是 ID 还是 slug
                const isNumeric = /^\d+$/.test(idOrSlug.toString())
                const apiProject = isNumeric 
                    ? await projectsApi.getById(idOrSlug)
                    : await projectsApi.getBySlug(idOrSlug.toString())
                setProject(transformProject(apiProject))
                setError(null)
            } catch (err) {
                console.error("Failed to fetch project detail:", err)
                setError(err instanceof Error ? err.message : "Failed to fetch project")
                // 降级到 mock 数据
                const found = mockProjects.find(p => p.id.toString() === idOrSlug.toString())
                setProject(found || null)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [idOrSlug])

    return { project, loading, error }
}

export function useBlogPosts() {
    const [data, setData] = useState<BlogPost[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!USE_REAL_API) {
            setData(mockBlogPosts)
            setLoading(false)
            return
        }

        const fetchData = async () => {
            try {
                setLoading(true)
                const apiPosts = await postsApi.getAll()
                setData(apiPosts.map(transformPost))
                setError(null)
            } catch (err) {
                console.error("Failed to fetch posts:", err)
                setError(err instanceof Error ? err.message : "Failed to fetch posts")
                // 降级到 mock 数据
                setData(mockBlogPosts)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [])

    const getPost = useCallback((id: string | number) => {
        return data.find((p) => p.id.toString() === id.toString() || p.slug === id.toString())
    }, [data])

    const getLatestPosts = useCallback((count: number = 3) => {
        return [...data].sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()).slice(0, count)
    }, [data])

    return { posts: data, getPost, getLatestPosts, loading, error }
}

export function usePostDetail(idOrSlug: string | number) {
    const [post, setPost] = useState<BlogPost | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!USE_REAL_API) {
            const found = mockBlogPosts.find(p => p.id.toString() === idOrSlug.toString())
            setPost(found || null)
            setLoading(false)
            return
        }

        const fetchData = async () => {
            try {
                setLoading(true)
                // 尝试判断是 ID 还是 slug
                const isNumeric = /^\d+$/.test(idOrSlug.toString())
                const apiPost = isNumeric 
                    ? await postsApi.getById(idOrSlug)
                    : await postsApi.getBySlug(idOrSlug.toString())
                setPost(transformPost(apiPost))
                setError(null)
            } catch (err) {
                console.error("Failed to fetch post detail:", err)
                setError(err instanceof Error ? err.message : "Failed to fetch post")
                // 降级到 mock 数据
                const found = mockBlogPosts.find(p => p.id.toString() === idOrSlug.toString())
                setPost(found || null)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [idOrSlug])

    return { post, loading, error }
}

export function useExperiences() {
    const [data, setData] = useState<Experience[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!USE_REAL_API) {
            setData(personalExperiences)
            setLoading(false)
            return
        }

        const fetchData = async () => {
            try {
                setLoading(true)
                const apiExperiences = await experiencesApi.getAll()
                setData(apiExperiences.map(transformExperience))
                setError(null)
            } catch (err) {
                console.error("Failed to fetch experiences:", err)
                setError(err instanceof Error ? err.message : "Failed to fetch experiences")
                // 降级到 mock 数据
                setData(personalExperiences)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [])

    return { experiences: data, loading, error }
}

export function useSocialLinks() {
    const [data, setData] = useState<SocialLink[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!USE_REAL_API) {
            setData(mockSocialLinks)
            setLoading(false)
            return
        }

        const fetchData = async () => {
            try {
                setLoading(true)
                const apiLinks = await socialLinksApi.getAll()
                setData(apiLinks.map(transformSocialLink))
                setError(null)
            } catch (err) {
                console.error("Failed to fetch social links:", err)
                setError(err instanceof Error ? err.message : "Failed to fetch social links")
                // 降级到 mock 数据
                setData(mockSocialLinks)
            } finally {
                setLoading(false)
            }
        }

        fetchData()
    }, [])

    return { socialLinks: data, loading, error }
}

export function usePersonalData() {
    const { experiences, loading: expLoading } = useExperiences()
    const { socialLinks, loading: socialLoading } = useSocialLinks()

    return {
        experiences,
        socialLinks,
        githubStats,
        roles,
        loading: expLoading || socialLoading,
    }
}

// ============ Comments Hook ============

import { commentsApi, type CommentDTO, type CreateCommentRequest } from "../services/api"

export function useComments(postId: number | null) {
    const [comments, setComments] = useState<CommentDTO[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [submitting, setSubmitting] = useState(false)

    // 获取评论列表
    const fetchComments = useCallback(async () => {
        if (!postId) return

        try {
            setLoading(true)
            const data = await commentsApi.getByPostId(postId)
            setComments(data)
            setError(null)
        } catch (err) {
            console.error("Failed to fetch comments:", err)
            setError(err instanceof Error ? err.message : "Failed to fetch comments")
        } finally {
            setLoading(false)
        }
    }, [postId])

    // 初始加载
    useEffect(() => {
        fetchComments()
    }, [fetchComments])

    // 提交评论
    const submitComment = useCallback(async (data: CreateCommentRequest): Promise<boolean> => {
        if (!postId) return false

        try {
            setSubmitting(true)
            await commentsApi.create(postId, data)
            // 重新获取评论列表
            await fetchComments()
            return true
        } catch (err) {
            console.error("Failed to submit comment:", err)
            setError(err instanceof Error ? err.message : "Failed to submit comment")
            return false
        } finally {
            setSubmitting(false)
        }
    }, [postId, fetchComments])

    // 获取评论数量（包括回复）
    const getCommentCount = useCallback(() => {
        const countReplies = (list: CommentDTO[]): number => {
            return list.reduce((acc, comment) => {
                return acc + 1 + (comment.replies ? countReplies(comment.replies) : 0)
            }, 0)
        }
        return countReplies(comments)
    }, [comments])

    return {
        comments,
        loading,
        error,
        submitting,
        submitComment,
        refreshComments: fetchComments,
        commentCount: getCommentCount(),
    }
}
