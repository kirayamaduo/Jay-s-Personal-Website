import { useState } from "react"
import { Link } from "react-router-dom"
import { motion } from "framer-motion"
import { ExternalLink, Github, Star, Play, Eye, Loader2 } from "lucide-react"
import { useProjects } from "@/hooks/useData"
import type { Project } from "@/types"

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.1 },
  },
}

const itemVariants = {
  hidden: { opacity: 0, y: 30 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.5 } },
}

function ProjectCard({ project }: { project: Project }) {
  const [isHovered, setIsHovered] = useState(false)

  return (
    <motion.div
      variants={itemVariants}
      className={`group relative overflow-hidden rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-primary/30 transition-all duration-300 ${project.featured ? "md:col-span-1" : ""
        }`}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Project Cover */}
      <div className={`relative aspect-video bg-gradient-to-br ${project.gradient || "from-gray-800 to-gray-900"} overflow-hidden`}>
        {/* Background Pattern */}
        <div className="absolute inset-0 opacity-30">
          <div className="absolute inset-0" style={{
            backgroundImage: "radial-gradient(circle at 1px 1px, rgba(255,255,255,0.15) 1px, transparent 0)",
            backgroundSize: "20px 20px"
          }} />
        </div>

        {/* Icon */}
        <motion.div
          className="absolute inset-0 flex items-center justify-center"
          animate={{ scale: isHovered ? 1.1 : 1 }}
          transition={{ duration: 0.3 }}
        >
          <span className="text-7xl filter drop-shadow-lg">{project.icon}</span>
        </motion.div>

        {/* Hover Overlay */}
        <motion.div
          className="absolute inset-0 bg-black/60 flex items-center justify-center gap-4"
          initial={{ opacity: 0 }}
          animate={{ opacity: isHovered ? 1 : 0 }}
          transition={{ duration: 0.2 }}
        >
          <Link
            to={`/projects/${project.id}`}
            className="flex items-center gap-2 px-4 py-2 rounded-lg bg-white/10 hover:bg-white/20 text-white text-sm font-medium transition-colors backdrop-blur-sm"
          >
            <Eye className="w-4 h-4" />
            查看详情
          </Link>
          {project.demo && (
            <a
              href={project.demo}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-2 px-4 py-2 rounded-lg bg-primary/80 hover:bg-primary text-primary-foreground text-sm font-medium transition-colors"
              onClick={(e) => e.stopPropagation()}
            >
              <Play className="w-4 h-4" />
              演示
            </a>
          )}
        </motion.div>

        {/* Featured Badge */}
        {project.featured && (
          <div className="absolute top-3 left-3 px-2 py-1 rounded-full bg-primary/80 text-primary-foreground text-xs font-medium backdrop-blur-sm">
            精选
          </div>
        )}

        {/* Stars Badge */}
        <div className="absolute top-3 right-3 flex items-center gap-1 px-2 py-1 rounded-full bg-black/40 text-white/80 text-xs backdrop-blur-sm">
          <Star className="w-3 h-3 fill-yellow-400 text-yellow-400" />
          {project.stars}
        </div>
      </div>

      {/* Project Info */}
      <div className="p-5">
        <h3 className="text-lg font-semibold text-foreground group-hover:text-primary transition-colors mb-2">
          {project.title}
        </h3>

        <p className="text-muted-foreground text-sm mb-4 line-clamp-2">
          {project.description}
        </p>

        {/* Tags */}
        <div className="flex flex-wrap gap-1.5 mb-4">
          {project.tags.slice(0, 3).map((tag) => (
            <span
              key={tag}
              className="px-2 py-0.5 text-xs rounded-md bg-zinc-800/80 text-zinc-400 font-mono"
            >
              {tag}
            </span>
          ))}
          {project.tags.length > 3 && (
            <span className="px-2 py-0.5 text-xs rounded-md bg-zinc-800/80 text-zinc-500">
              +{project.tags.length - 3}
            </span>
          )}
        </div>

        {/* Actions */}
        <div className="flex items-center gap-2">
          <a
            href={project.github}
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white/5 hover:bg-white/10 text-xs text-foreground transition-colors"
            onClick={(e) => e.stopPropagation()}
          >
            <Github className="w-3.5 h-3.5" />
            Source
          </a>
          {project.demo && (
            <a
              href={project.demo}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-primary/10 hover:bg-primary/20 text-xs text-primary transition-colors"
              onClick={(e) => e.stopPropagation()}
            >
              <ExternalLink className="w-3.5 h-3.5" />
              Live
            </a>
          )}
        </div>
      </div>
    </motion.div>
  )
}

export default function Projects() {
  const { projects, loading } = useProjects()

  if (loading) {
    return (
      <div className="mx-auto max-w-6xl text-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-primary mx-auto mb-4" />
        <p className="text-muted-foreground">加载项目中...</p>
      </div>
    )
  }

  return (
    <motion.div
      className="mx-auto max-w-6xl"
      variants={containerVariants}
      initial="hidden"
      animate="visible"
    >
      {/* Header */}
      <motion.div className="mb-12 text-center" variants={itemVariants}>
        <h1 className="text-4xl font-bold text-foreground mb-4">创造物</h1>
        <p className="text-muted-foreground text-lg">一些我引以为傲的项目</p>
      </motion.div>

      {/* Featured Projects */}
      <motion.div className="mb-8" variants={itemVariants}>
        <h2 className="text-sm font-medium text-muted-foreground uppercase tracking-wider mb-4">
          精选项目
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {projects.filter(p => p.featured).map((project) => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      </motion.div>

      {/* Other Projects */}
      <motion.div variants={itemVariants}>
        <h2 className="text-sm font-medium text-muted-foreground uppercase tracking-wider mb-4">
          更多项目
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {projects.filter(p => !p.featured).map((project) => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      </motion.div>
    </motion.div>
  )
}
