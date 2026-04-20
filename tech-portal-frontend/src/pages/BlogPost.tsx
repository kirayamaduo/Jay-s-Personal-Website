import { useParams, Link } from "react-router-dom"
import { motion, useScroll, useSpring } from "framer-motion"
import { ArrowLeft, Clock, Calendar, Tag, Share2, Loader2 } from "lucide-react"
import { MarkdownRenderer } from "../components/MarkdownRenderer"
import { usePostDetail } from "@/hooks/useData"
import { TableOfContents } from "@/components/TableOfContents"
import { CommentSection } from "@/components/CommentSection"
import { Helmet } from "react-helmet-async"

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

export default function BlogPost() {
  const { id } = useParams<{ id: string }>()
  const { post, loading, error } = usePostDetail(id || "")

  const { scrollYProgress } = useScroll()
  const scaleX = useSpring(scrollYProgress, {
    stiffness: 100,
    damping: 30,
    restDelta: 0.001
  })

  if (loading) {
    return (
      <div className="mx-auto max-w-4xl text-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-primary mx-auto mb-4" />
        <p className="text-muted-foreground">加载中...</p>
      </div>
    )
  }

  if (!post) {
    return (
      <div className="mx-auto max-w-4xl text-center py-20">
        <h1 className="text-2xl font-bold text-foreground mb-4">文章不存在</h1>
        {error && <p className="text-red-400 mb-4">{error}</p>}
        <Link to="/blog" className="text-primary hover:underline">
          返回博客列表
        </Link>
      </div>
    )
  }



  return (
    <>
      <Helmet>
        <title>{post.title} | Alex's Blog</title>
        <meta name="description" content={post.excerpt} />
      </Helmet>
      {/* Reading Progress Bar */}
      <motion.div
        className="fixed top-0 left-0 right-0 h-1 bg-primary z-50 origin-left"
        style={{ scaleX }}
      />

      <motion.article
        className="mx-auto max-w-4xl"
        variants={containerVariants}
        initial="hidden"
        animate="visible"
      >
        {/* Back Button */}
        <motion.div variants={itemVariants} className="mb-8">
          <Link
            to="/blog"
            className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            返回博客
          </Link>
        </motion.div>

        {/* Header */}
        <motion.header variants={itemVariants} className="mb-12">
          <div className="flex flex-wrap items-center gap-4 text-sm text-muted-foreground mb-4">
            <div className="flex items-center gap-1.5">
              <Calendar className="w-4 h-4" />
              {post.date}
            </div>
            <div className="flex items-center gap-1.5">
              <Clock className="w-4 h-4" />
              {post.readTime}
            </div>
          </div>

          <h1 className="text-3xl md:text-5xl font-bold text-foreground mb-6 leading-tight">
            {post.title}
          </h1>

          <div className="flex items-center justify-between gap-4">
            <div className="flex flex-wrap gap-2">
              {post.tags?.map((tag) => (
                <div
                  key={tag}
                  className="flex items-center gap-1 px-3 py-1 rounded-full bg-primary/10 text-primary text-xs font-medium"
                >
                  <Tag className="w-3 h-3" />
                  {tag}
                </div>
              ))}
            </div>

            <button className="p-2 rounded-full hover:bg-white/10 text-muted-foreground hover:text-foreground transition-colors">
              <Share2 className="w-5 h-5" />
            </button>
          </div>
        </motion.header>



        {/* Content Layout with TOC */}
        <div className="grid grid-cols-1 xl:grid-cols-[1fr_250px] gap-8">
          <motion.div
            variants={itemVariants}
            className="p-8 md:p-12 rounded-3xl bg-black/40 border border-white/10 backdrop-blur-md"
          >
            <MarkdownRenderer content={post.content} />
          </motion.div>

          {/* TOC Sidebar */}
          <div className="hidden xl:block">
            <TableOfContents content={post.content} />
          </div>
        </div>

        {/* Footer */}
        <motion.div variants={itemVariants} className="mt-12 pt-8 border-t border-white/10 text-center">
          <p className="text-muted-foreground italic">
            感谢阅读！如果你觉得这篇文章对你有帮助，欢迎分享给更多人。
          </p>
        </motion.div>

        {/* Comments Section */}
        <motion.div variants={itemVariants}>
          <CommentSection postId={typeof post.id === 'number' ? post.id : parseInt(post.id as string, 10)} />
        </motion.div>
      </motion.article>
    </>
  )
}
