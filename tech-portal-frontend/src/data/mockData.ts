import { Github, Linkedin, Twitter, Mail, GitFork, Star, Users } from "lucide-react"
import type { Experience, SocialLink, GithubStat, Project, BlogPost } from "../types"

// --- About Page Data ---

export const personalExperiences: Experience[] = [
    {
        id: 1,
        type: "work",
        title: "Senior Full Stack Developer",
        company: "TechCorp Inc.",
        location: "San Francisco, CA",
        period: "2022 - Present",
        description: "领导团队开发核心产品，优化系统架构，提升性能 40%。",
    },
    {
        id: 2,
        type: "work",
        title: "Full Stack Developer",
        company: "StartupXYZ",
        location: "Remote",
        period: "2020 - 2022",
        description: "从零构建公司官网和内部管理系统，独立完成全栈开发。",
    },
    {
        id: 3,
        type: "education",
        title: "Computer Science, B.S.",
        company: "University of Technology",
        location: "Beijing, China",
        period: "2016 - 2020",
        description: "主修计算机科学，GPA 3.8/4.0。",
    },
]

export const socialLinks: SocialLink[] = [
    { icon: Github, label: "GitHub", href: "https://github.com", username: "@alexchen" },
    { icon: Twitter, label: "Twitter", href: "https://twitter.com", username: "@alexchen" },
    { icon: Linkedin, label: "LinkedIn", href: "https://linkedin.com", username: "Alex Chen" },
    { icon: Mail, label: "Email", href: "mailto:hello@example.com", username: "hello@example.com" },
]

export const githubStats: GithubStat[] = [
    { icon: GitFork, label: "Contributions", value: "2,847" },
    { icon: Star, label: "Stars Earned", value: "892" },
    { icon: Users, label: "Followers", value: "1.2k" },
]

// --- Home Page Data ---

export const roles = ["Full Stack Developer", "Open Source Lover", "Tech Writer", "Coffee Addict ☕"]

// --- Project Data ---

// Combined data from Projects.tsx (list) and ProjectDetail.tsx (details)
// We use a comprehensive list here.

export const projects: Project[] = [
    {
        id: 1,
        title: "CloudSync",
        description: "实时文件同步平台，支持多端协作和版本管理。采用 WebSocket 实现实时同步，支持断点续传。",
        longDescription: `CloudSync 是一个企业级的文件同步解决方案，旨在为团队提供高效、安全的文件协作体验。

项目采用微服务架构，使用 WebSocket 实现实时双向通信，确保文件变更能够即时同步到所有设备。支持断点续传、冲突解决和版本历史回溯等高级功能。`,
        gradient: "from-blue-500/20 via-cyan-500/20 to-teal-500/20",
        icon: "☁️",
        tags: ["Next.js", "AWS S3", "TypeScript", "WebSocket", "Redis", "PostgreSQL"],
        github: "https://github.com",
        demo: "https://demo.com",
        stars: 128,
        forks: 34,
        contributors: 8,
        lastUpdate: "2024-01-10",
        featured: true,
        features: [
            "实时文件同步，延迟低于 100ms",
            "支持大文件断点续传",
            "智能冲突检测与解决",
            "完整的版本历史记录",
            "端到端加密传输",
            "跨平台客户端支持",
        ],
        techStack: [
            { name: "Next.js", description: "React 全栈框架，用于构建 Web 应用" },
            { name: "AWS S3", description: "对象存储服务，用于文件持久化" },
            { name: "WebSocket", description: "实时双向通信协议" },
            { name: "Redis", description: "内存数据库，用于会话和缓存" },
        ],
        screenshots: [
            "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&q=80&w=800&h=600",
            "https://images.unsplash.com/photo-1551650975-87deedd944c3?auto=format&fit=crop&q=80&w=800&h=600",
            "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&q=80&w=800&h=600"
        ],
    },
    {
        id: 2,
        title: "DevMetrics",
        description: "开发者数据分析仪表盘，可视化展示代码统计、提交频率和项目健康度。",
        longDescription: `DevMetrics 帮助开发团队了解代码质量和开发效率。通过可视化图表展示代码统计、提交频率、代码审查速度等关键指标。

支持 GitHub、GitLab 等主流代码托管平台的数据接入。`,
        gradient: "from-purple-500/20 via-pink-500/20 to-rose-500/20",
        icon: "📊",
        tags: ["React", "D3.js", "Node.js", "PostgreSQL", "GraphQL"],
        github: "https://github.com",
        demo: "https://demo.com",
        stars: 256,
        forks: 67,
        contributors: 12,
        lastUpdate: "2024-01-08",
        featured: true,
        features: [
            "代码提交频率热力图",
            "代码行数统计趋势",
            "PR 审核时间分析",
            "团队贡献度排行",
            "自定义仪表盘",
            "Slack/Discord 通知集成",
        ],
        techStack: [
            { name: "React", description: "用户界面框架" },
            { name: "D3.js", description: "数据可视化库" },
            { name: "Node.js", description: "后端运行时" },
            { name: "GraphQL", description: "API 查询语言" },
        ],
        screenshots: [],
    },
    {
        id: 3,
        title: "AIChat",
        description: "AI 驱动的智能对话系统，支持多轮对话、上下文理解和个性化回复。",
        longDescription: `AIChat 是一个基于大语言模型的智能对话系统，支持多轮对话、上下文理解和个性化回复。

系统采用 RAG（检索增强生成）技术，可以根据用户提供的知识库进行精准回答。`,
        gradient: "from-green-500/20 via-emerald-500/20 to-teal-500/20",
        icon: "🤖",
        tags: ["Python", "FastAPI", "OpenAI", "Redis", "LangChain", "Pinecone"],
        github: "https://github.com",
        demo: "https://demo.com",
        stars: 512,
        forks: 123,
        contributors: 15,
        lastUpdate: "2024-01-12",
        featured: true,
        features: [
            "多轮对话上下文记忆",
            "支持自定义知识库",
            "流式响应输出",
            "多模型切换支持",
            "对话历史导出",
            "API 接口开放",
        ],
        techStack: [
            { name: "FastAPI", description: "高性能 Python Web 框架" },
            { name: "LangChain", description: "LLM 应用开发框架" },
            { name: "Pinecone", description: "向量数据库" },
            { name: "Redis", description: "会话状态存储" },
        ],
        screenshots: [],
    },
    {
        id: 4,
        title: "MarkdownEditor",
        description: "所见即所得的 Markdown 编辑器，支持实时预览、代码高亮和导出多种格式。",
        longDescription: "一个现代化的 Markdown 编辑器...", // Simplified for non-detail items unless we have full data
        gradient: "from-orange-500/20 via-amber-500/20 to-yellow-500/20",
        icon: "✏️",
        tags: ["React", "CodeMirror", "Electron"],
        github: "https://github.com",
        stars: 89,
        featured: false,
    },
    {
        id: 5,
        title: "TaskFlow",
        description: "可视化任务管理工具，支持看板视图、甘特图和团队协作。",
        longDescription: "TaskFlow 让项目管理变得简单...",
        gradient: "from-indigo-500/20 via-violet-500/20 to-purple-500/20",
        icon: "📋",
        tags: ["Vue 3", "Pinia", "Supabase"],
        github: "https://github.com",
        demo: "https://demo.com",
        stars: 167,
        featured: false,
    },
    {
        id: 6,
        title: "PixelArt",
        description: "在线像素画编辑器，支持动画制作、图层管理和社区分享。",
        longDescription: "创作你的像素艺术作品...",
        gradient: "from-pink-500/20 via-rose-500/20 to-red-500/20",
        icon: "🎨",
        tags: ["Canvas", "React", "Firebase"],
        github: "https://github.com",
        demo: "https://demo.com",
        stars: 234,
        featured: false,
    },
]

// --- Blog Components Data ---

export const blogPosts: BlogPost[] = [
    {
        id: 1,
        title: "构建现代化的 React 应用架构",
        excerpt: "探索 2024 年最佳的 React 项目结构和设计模式，包括状态管理、路由和性能优化...",
        date: "2024-01-15",
        tags: ["React", "Architecture"],
        readTime: "8 min",
        content: `
# 构建现代化的 React 应用架构

在 2024 年，构建一个可维护、可扩展的 React 应用需要考虑很多方面。本文将探讨一些最佳实践和设计模式。

## 项目结构

一个好的项目结构是成功的一半。我推荐按照功能模块来组织代码：

\`\`\`
src/
├── components/     # 通用组件
├── pages/          # 页面组件
├── hooks/          # 自定义 Hooks
├── utils/          # 工具函数
├── services/       # API 服务
└── types/          # TypeScript 类型定义
\`\`\`

## 状态管理

对于状态管理，我推荐根据复杂度选择合适的方案：

- **简单应用**：使用 React 内置的 \`useState\` 和 \`useContext\`
- **中等复杂度**：使用 Zustand 或 Jotai
- **复杂应用**：使用 Redux Toolkit

### 使用 Zustand 的示例

\`\`\`typescript
import { create } from 'zustand'

interface UserStore {
  user: User | null
  setUser: (user: User) => void
  logout: () => void
}

export const useUserStore = create<UserStore>((set) => ({
  user: null,
  setUser: (user) => set({ user }),
  logout: () => set({ user: null }),
  }))
\`\`\`

## 性能优化

性能优化是构建大型应用的关键。以下是一些常用的优化技巧：

1. **代码分割**：使用 \`React.lazy\` 和 \`Suspense\` 进行路由级别的代码分割
2. **Memoization**：合理使用 \`useMemo\` 和 \`useCallback\`
3. **虚拟列表**：对于长列表使用 \`react-window\` 或 \`react-virtualized\`

> 记住：过早优化是万恶之源。只在确定存在性能问题时才进行优化。

## 测试策略

一个完整的测试策略应该包括：

| 测试类型 | 工具 | 覆盖范围 |
|---------|------|---------|
| 单元测试 | Jest | 工具函数、Hooks |
| 组件测试 | React Testing Library | UI 组件 |
| E2E 测试 | Playwright | 用户流程 |

## 总结

构建现代化的 React 应用需要综合考虑项目结构、状态管理、性能优化和测试策略。选择适合团队和项目的技术栈，保持代码的简洁和可维护性。

---

希望这篇文章对你有所帮助！如果有任何问题，欢迎在评论区讨论。
`,
    },
    {
        id: 2,
        title: "Tailwind CSS v4 新特性解析",
        excerpt: "深入了解 Tailwind CSS 4.0 带来的革命性变化：新的配置系统、性能提升和更好的开发体验...",
        date: "2024-01-10",
        tags: ["CSS", "Tailwind"],
        readTime: "5 min",
        content: `
# Tailwind CSS v4 新特性解析

Tailwind CSS 4.0 带来了许多令人兴奋的新特性，让我们一起来看看。

## 全新的配置系统

v4 引入了基于 CSS 的配置方式，告别了 \`tailwind.config.js\`：

\`\`\`css
@theme {
  --color-primary: oklch(0.75 0.15 180);
  --font-sans: 'Inter', sans-serif;
  --spacing-lg: 2rem;
}
\`\`\`

## 性能提升

新版本在构建速度上有显著提升：

- **增量编译**：只重新编译改变的部分
- **更小的输出**：生成的 CSS 文件更小
- **更快的 HMR**：热更新速度提升 10 倍

## 新的颜色系统

v4 默认使用 OKLCH 颜色空间，提供更好的颜色一致性：

\`\`\`css
.text-primary {
  color: oklch(0.75 0.15 180);
}
\`\`\`

## 总结

Tailwind CSS v4 是一次重大更新，带来了更好的开发体验和性能。建议新项目直接使用 v4，老项目可以逐步迁移。
`,
    },
    {
        id: 3,
        title: "TypeScript 高级类型编程",
        excerpt: "掌握 TypeScript 中的条件类型、映射类型和模板字面量类型，写出更安全的代码...",
        date: "2024-01-05",
        tags: ["TypeScript"],
        readTime: "12 min",
        content: `
# TypeScript 高级类型编程

掌握 TypeScript 的高级类型可以让你写出更安全、更灵活的代码。

## 条件类型

条件类型让你可以根据条件选择不同的类型：

\`\`\`typescript
type IsString<T> = T extends string ? true : false

type A = IsString<string>  // true
type B = IsString<number>  // false
\`\`\`

## 映射类型

映射类型可以基于现有类型创建新类型：

\`\`\`typescript
type Readonly<T> = {
  readonly [K in keyof T]: T[K]
}

type Optional<T> = {
  [K in keyof T]?: T[K]
}
\`\`\`

## 模板字面量类型

v4.1 引入的模板字面量类型非常强大：

\`\`\`typescript
type EventName<T extends string> = \`on\${Capitalize<T>}\`

type ClickEvent = EventName<'click'>  // 'onClick'
\`\`\`

## 实战示例

来看一个实际的例子，创建一个类型安全的 API 客户端：

\`\`\`typescript
type ApiEndpoints = {
  '/users': { GET: User[]; POST: User }
  '/users/:id': { GET: User; PUT: User; DELETE: void }
}

type ApiClient = {
  [Path in keyof ApiEndpoints]: {
    [Method in keyof ApiEndpoints[Path]]: 
      () => Promise<ApiEndpoints[Path][Method]>
  }
}
\`\`\`

## 总结

TypeScript 的类型系统是图灵完备的，掌握这些高级类型可以让你的代码更加安全和灵活。
`,
    },
    {
        id: 4,
        title: "从零搭建个人博客系统",
        excerpt: "使用 React + Spring Boot 构建一个功能完整的博客系统，包括 Markdown 渲染和评论功能...",
        date: "2024-01-01",
        tags: ["React", "Spring Boot"],
        readTime: "15 min",
        content: `
# 从零搭建个人博客系统

本文将介绍如何使用 React + Spring Boot 构建一个功能完整的博客系统。

## 技术栈选择

### 前端
- React 18 + TypeScript
- Tailwind CSS
- React Router
- Framer Motion

### 后端
- Spring Boot 3
- JDK 17
- MySQL 8
- Redis

## 架构设计

采用前后端分离的架构：

\`\`\`
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Browser   │────▶│   Nginx     │────▶│  Spring Boot│
└─────────────┘     └─────────────┘     └─────────────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │   MySQL     │
                                        └─────────────┘
\`\`\`

## 核心功能

1. **文章管理**：Markdown 编辑器、分类标签
2. **评论系统**：支持嵌套回复
3. **用户系统**：JWT 认证
4. **SEO 优化**：服务端渲染

## 总结

搭建个人博客是学习全栈开发的绝佳实践项目，希望这篇文章能给你一些启发。
`,
    },
]
