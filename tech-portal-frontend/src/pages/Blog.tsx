import { useState } from "react"
import { Link } from "react-router-dom"
import { motion } from "framer-motion"
import { FileText, Search, Loader2 } from "lucide-react"
import { useBlogPosts } from "@/hooks/useData"

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.1 },
  },
}

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.5 } },
}

export default function Blog() {
  const { posts, loading } = useBlogPosts()
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedTag, setSelectedTag] = useState("All")

  if (loading) {
    return (
      <div className="mx-auto max-w-4xl text-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-primary mx-auto mb-4" />
        <p className="text-muted-foreground">加载文章中...</p>
      </div>
    )
  }

  const filteredPosts = posts.filter(post => {
    const matchesSearch = post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.excerpt?.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesTag = selectedTag === "All" || post.tags?.includes(selectedTag)
    return matchesSearch && matchesTag
  })

  // Get all unique tags from posts
  const allTags = ["All", ...Array.from(new Set(posts.flatMap(post => post.tags || [])))]

  return (
    <motion.div
      className="mx-auto max-w-4xl"
      variants={containerVariants}
      initial="hidden"
      animate="visible"
    >
      {/* Header */}
      <motion.div className="mb-12 text-center" variants={itemVariants}>
        <h1 className="text-4xl font-bold text-foreground mb-4">博客花园</h1>
        <p className="text-muted-foreground text-lg">记录技术探索与生活感悟</p>
      </motion.div>

      {/* Search Bar */}
      <motion.div className="mb-8" variants={itemVariants}>
        <div className="relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
          <input
            type="text"
            placeholder="搜索文章..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-12 pr-4 py-3 rounded-xl bg-black/40 border border-white/10 text-foreground placeholder:text-muted-foreground focus:outline-none focus:border-primary/50 backdrop-blur-md transition-colors"
          />
        </div>
      </motion.div>

      {/* Tags Filter */}
      <motion.div className="flex flex-wrap gap-2 mb-8" variants={itemVariants}>
        {allTags.map((tag) => (
          <button
            key={tag}
            onClick={() => setSelectedTag(tag)}
            className={`px-4 py-2 rounded-full text-sm font-medium transition-all ${selectedTag === tag
              ? "bg-primary text-primary-foreground"
              : "bg-white/5 text-muted-foreground hover:bg-white/10 hover:text-foreground"
              }`}
          >
            {tag}
          </button>
        ))}
      </motion.div>

      {/* Posts List */}
      <div className="space-y-6">
        {filteredPosts.length > 0 ? (
          filteredPosts.map((post) => (
            <motion.article key={post.id} variants={itemVariants} layout>
              <Link
                to={`/blog/${post.id}`}
                className="group block p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-primary/30 hover:bg-black/50 transition-all"
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <span className="text-sm text-muted-foreground">{post.date}</span>
                      <span className="text-muted-foreground">·</span>
                      <span className="text-sm text-muted-foreground">{post.readTime}</span>
                    </div>
                    <h2 className="text-xl font-semibold text-foreground group-hover:text-primary transition-colors mb-2">
                      {post.title}
                    </h2>
                    <p className="text-muted-foreground line-clamp-2 mb-4">{post.excerpt}</p>
                    <div className="flex flex-wrap gap-2">
                      {post.tags?.map((tag) => (
                        <span
                          key={tag}
                          className="px-2 py-1 text-xs rounded-md bg-zinc-800 text-zinc-300 font-mono"
                        >
                          {tag}
                        </span>
                      ))}
                    </div>
                  </div>
                  <FileText className="w-8 h-8 text-muted-foreground group-hover:text-primary transition-colors shrink-0" />
                </div>
              </Link>
            </motion.article>
          ))
        ) : (
          <motion.div variants={itemVariants} className="text-center py-12 text-muted-foreground">
            没有找到相关文章
          </motion.div>
        )}
      </div>
    </motion.div>
  )
}
