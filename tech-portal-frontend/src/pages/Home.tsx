import { Link } from "react-router-dom"
import { motion, type Variants } from "framer-motion"
import { ArrowRight, FileText, Folder } from "lucide-react"
import { Typewriter } from "@/components/Typewriter"
import { NowPlaying } from "@/components/NowPlaying"
import { StatusIndicator } from "@/components/StatusIndicator"
import { GithubActivityCard } from "@/components/GithubActivityCard"
import { Helmet } from "react-helmet-async"
import { usePersonalData, useBlogPosts, useProjects } from "@/hooks/useData"

const containerVariants: Variants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.15 },
  },
}

const itemVariants: Variants = {
  hidden: { opacity: 0, y: 30 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.6, ease: "easeOut" },
  },
}

export default function Home() {
  const { roles } = usePersonalData()
  const { getLatestPosts } = useBlogPosts()
  const { getFeaturedProjects } = useProjects()

  // Get data from hooks
  const latestPosts = getLatestPosts(2)
  const featuredProjects = getFeaturedProjects().slice(0, 2)

  return (
    <motion.div
      className="mx-auto max-w-5xl"
      variants={containerVariants}
      initial="hidden"
      animate="visible"
    >
      <Helmet>
        <title>Alex Chen | Full Stack Developer</title>
        <meta name="description" content="Alex Chen's personal website regarding technology and coding." />
      </Helmet>
      {/* Hero Section */}
      <motion.section className="py-16 md:py-20" variants={itemVariants}>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">
          {/* Main Hero Content */}
          <div className="lg:col-span-2">
            <motion.div
              className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-primary/10 border border-primary/20 text-primary text-sm mb-6"
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ delay: 0.2 }}
            >
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-primary opacity-75" />
                <span className="relative inline-flex rounded-full h-2 w-2 bg-primary" />
              </span>
              <span>Available for work</span>
            </motion.div>

            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground mb-4">
              Hi, I'm <span className="text-primary">Alex Chen</span>
            </h1>

            <div className="text-xl md:text-2xl text-muted-foreground mb-6 h-8">
              <Typewriter words={roles} className="text-primary font-medium" />
            </div>

            <p className="text-lg text-muted-foreground max-w-xl mb-8 leading-relaxed">
              热衷于构建优雅且高效的数字产品。在这里记录技术探索与生活感悟。
            </p>

            <div className="flex flex-wrap items-center gap-4">
              <Link
                to="/about"
                className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-primary text-primary-foreground font-medium hover:bg-primary/90 transition-colors"
              >
                了解更多
                <ArrowRight className="w-4 h-4" />
              </Link>
              <Link
                to="/blog"
                className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-white/5 border border-white/10 text-foreground font-medium hover:bg-white/10 transition-colors"
              >
                阅读博客
              </Link>
            </div>
          </div>

          {/* Side Widgets - Hidden on mobile, visible on desktop */}
          <div className="hidden lg:block space-y-4">
            <StatusIndicator status="coding" />
            <NowPlaying song="Midnight City" artist="M83" />
            <GithubActivityCard />
          </div>
        </div>
      </motion.section>

      {/* Latest Posts Section */}
      <motion.section className="py-12" variants={itemVariants}>
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-2xl font-semibold text-foreground flex items-center gap-3">
            <FileText className="w-6 h-6 text-primary" />
            最新文章
          </h2>
          <Link
            to="/blog"
            className="text-sm text-muted-foreground hover:text-primary flex items-center gap-1 transition-colors"
          >
            查看全部
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {latestPosts.map((post) => (
            <Link to={`/blog/${post.id}`} key={post.id} className="block h-full">
              <motion.article
                variants={itemVariants}
                className="group p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-primary/30 hover:bg-black/50 transition-all cursor-pointer h-full"
              >
                <div className="flex items-center gap-3 mb-3 text-sm text-muted-foreground">
                  <span>{post.date}</span>
                  <span>·</span>
                  <span>{post.readTime}</span>
                </div>
                <h3 className="text-lg font-semibold text-foreground group-hover:text-primary transition-colors mb-2">
                  {post.title}
                </h3>
                <p className="text-muted-foreground text-sm line-clamp-2">{post.excerpt}</p>
              </motion.article>
            </Link>
          ))}
        </div>
      </motion.section>

      {/* Featured Projects Section */}
      <motion.section className="py-12" variants={itemVariants}>
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-2xl font-semibold text-foreground flex items-center gap-3">
            <Folder className="w-6 h-6 text-primary" />
            精选项目
          </h2>
          <Link
            to="/projects"
            className="text-sm text-muted-foreground hover:text-primary flex items-center gap-1 transition-colors"
          >
            更多项目
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {featuredProjects.map((project) => (
            <motion.div
              key={project.id}
              variants={itemVariants}
              className="group p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-primary/30 hover:bg-black/50 transition-all"
            >
              <h3 className="text-lg font-semibold text-foreground group-hover:text-primary transition-colors mb-2">
                {project.title}
              </h3>
              <p className="text-muted-foreground text-sm mb-4">{project.description}</p>
              <div className="flex flex-wrap gap-2">
                {project.tags.slice(0, 3).map((tag) => (
                  <span
                    key={tag}
                    className="px-2 py-1 text-xs rounded-md bg-zinc-800 text-zinc-300 font-mono"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </motion.div>
          ))}
        </div>
      </motion.section>

      {/* CTA Section */}
      <motion.section
        className="py-12 text-center"
        variants={itemVariants}
      >
        <div className="p-8 md:p-12 rounded-3xl bg-gradient-to-br from-primary/10 to-purple-500/10 border border-white/10 backdrop-blur-md">
          <h2 className="text-2xl md:text-3xl font-bold text-foreground mb-4">
            想要合作或聊聊？
          </h2>
          <p className="text-muted-foreground mb-6 max-w-lg mx-auto">
            无论是项目合作、技术交流，还是单纯想打个招呼，都欢迎联系我。
          </p>
          <a
            href="mailto:hello@example.com"
            className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-primary text-primary-foreground font-medium hover:bg-primary/90 transition-colors"
          >
            发送邮件
            <ArrowRight className="w-4 h-4" />
          </a>
        </div>
      </motion.section>
    </motion.div>
  )
}
