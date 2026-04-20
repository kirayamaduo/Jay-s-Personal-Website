import type { LucideIcon } from "lucide-react"

// ============ 前端使用的类型（用于组件和 UI） ============

export interface SocialLink {
    icon: LucideIcon
    label: string
    href: string
    username: string
}

export interface Experience {
    id: number
    type: "work" | "education"
    title: string
    company: string
    location: string
    period: string
    description: string
    achievements?: string[]
}

export interface GithubStat {
    icon: LucideIcon
    label: string
    value: string
}

export interface Project {
    id: number | string
    title: string
    slug?: string
    description: string
    longDescription?: string
    gradient?: string
    icon?: string
    tags: string[]
    github: string
    demo?: string
    stars: number
    featured: boolean

    // Detailed fields
    forks?: number
    contributors?: number
    lastUpdate?: string
    features?: string[]
    techStack?: { name: string; description: string }[]
    screenshots?: string[]
}

export interface BlogPost {
    id: number | string
    title: string
    slug?: string
    excerpt: string
    date: string
    readTime: string
    tags: string[]
    content: string
    coverImage?: string
    authorName?: string
    viewCount?: number
}

// ============ API 类型（从后端返回的原始数据） ============

export interface ApiTag {
    id: number
    name: string
    slug: string
    color: string | null
    description: string | null
}

export interface ApiProject {
    id: number
    title: string
    slug: string
    description: string
    content?: string
    icon: string | null
    gradientClass: string | null
    githubUrl: string | null
    demoUrl: string | null
    starsCount: number | null
    forksCount: number | null
    contributorsCount?: number | null
    lastCommitAt?: string | null
    techStack: Array<{ name: string; description: string }> | null
    features?: string[] | null
    galleryImages?: string[] | null
    isFeatured: boolean
    displayOrder: number
    createdAt?: string
    updatedAt?: string
    tags: ApiTag[]
}

export interface ApiPost {
    id: number
    title: string
    slug: string
    excerpt: string | null
    content?: string
    renderedContent?: string | null
    coverImage: string | null
    isFeatured: boolean
    viewCount: number
    readingTimeMinutes: number | null
    publishedAt: string
    createdAt?: string
    updatedAt?: string
    authorId?: number | null
    authorName: string | null
    authorAvatar?: string | null
    tags: ApiTag[]
}

export interface ApiExperience {
    id: number
    type: 'work' | 'education'
    title: string
    company: string
    location: string | null
    startDate: string
    endDate: string | null
    description: string | null
    achievements: string[] | null
    displayOrder: number
}

export interface ApiSocialLink {
    id: number
    platform: string
    icon: string
    label: string
    url: string
    username: string | null
    displayOrder: number
}
