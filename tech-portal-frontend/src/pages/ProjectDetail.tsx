import { useParams, Link } from "react-router-dom"
import { motion } from "framer-motion"
import { ArrowLeft, ExternalLink, Github, Star, Calendar, Users, GitFork, Loader2 } from "lucide-react"
import { useProjectDetail } from "@/hooks/useData"
import useEmblaCarousel from "embla-carousel-react"
import Autoplay from "embla-carousel-autoplay"
import { Helmet } from "react-helmet-async"

const ProjectGallery = ({ images }: { images: string[] }) => {
  const [emblaRef] = useEmblaCarousel({ loop: true }, [Autoplay({ delay: 4000 })])

  if (!images || images.length === 0) return null

  return (
    <div className="overflow-hidden rounded-2xl border border-white/10 bg-black/20" ref={emblaRef}>
      <div className="flex">
        {images.map((src, index) => (
          <div key={index} className="flex-[0_0_100%] min-w-0">
            <div className="aspect-video relative">
              <img
                src={src}
                alt={`Screenshot ${index + 1}`}
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-x-0 bottom-0 p-4 bg-gradient-to-t from-black/80 to-transparent">
                <span className="text-xs text-white/80">Project Screenshot {index + 1}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

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

export default function ProjectDetail() {
  const { id } = useParams<{ id: string }>()
  const { project, loading, error } = useProjectDetail(id || "")

  if (loading) {
    return (
      <div className="mx-auto max-w-4xl text-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-primary mx-auto mb-4" />
        <p className="text-muted-foreground">加载中...</p>
      </div>
    )
  }

  if (!project) {
    return (
      <div className="mx-auto max-w-4xl text-center py-20">
        <h1 className="text-2xl font-bold text-foreground mb-4">项目不存在</h1>
        {error && <p className="text-red-400 mb-4">{error}</p>}
        <Link to="/projects" className="text-primary hover:underline">
          返回项目列表
        </Link>
      </div>
    )
  }



  return (
    <motion.div
      className="mx-auto max-w-4xl"
      variants={containerVariants}
      initial="hidden"
      animate="visible"
    >
      <Helmet>
        <title>{project.title} | Alex's Projects</title>
        <meta name="description" content={project.description} />
      </Helmet>
      {/* Back Button */}
      <motion.div variants={itemVariants} className="mb-8">
        <Link
          to="/projects"
          className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          返回项目
        </Link>
      </motion.div>

      {/* Hero Section */}
      <motion.div
        variants={itemVariants}
        className={`relative rounded-3xl bg-gradient-to-br ${project.gradient || "from-gray-800 to-gray-900"} p-8 md:p-12 mb-8 overflow-hidden`}
      >
        {/* Background Pattern */}
        <div className="absolute inset-0 opacity-30">
          <div className="absolute inset-0" style={{
            backgroundImage: "radial-gradient(circle at 1px 1px, rgba(255,255,255,0.15) 1px, transparent 0)",
            backgroundSize: "24px 24px"
          }} />
        </div>

        <div className="relative z-10">
          <span className="text-6xl mb-4 block">{project.icon}</span>
          <h1 className="text-3xl md:text-4xl font-bold text-foreground mb-3">
            {project.title}
          </h1>
          <p className="text-lg text-muted-foreground mb-6 max-w-2xl">
            {project.description}
          </p>

          {/* Actions */}
          <div className="flex flex-wrap items-center gap-3">
            <a
              href={project.github}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-white/10 hover:bg-white/20 text-foreground font-medium transition-colors backdrop-blur-sm"
            >
              <Github className="w-5 h-5" />
              查看源码
            </a>
            {project.demo && (
              <a
                href={project.demo}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-primary text-primary-foreground font-medium hover:bg-primary/90 transition-colors"
              >
                <ExternalLink className="w-5 h-5" />
                在线演示
              </a>
            )}
          </div>
        </div>
      </motion.div>


      {/* Gallery */}
      <motion.div variants={itemVariants} className="mb-8">
        <ProjectGallery images={project.screenshots || []} />
      </motion.div>

      {/* Stats */}
      <motion.div
        variants={itemVariants}
        className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8"
      >
        {[
          { icon: Star, label: "Stars", value: project.stars, color: "text-yellow-400" },
          { icon: GitFork, label: "Forks", value: project.forks || 0, color: "text-blue-400" },
          { icon: Users, label: "贡献者", value: project.contributors || 1, color: "text-green-400" },
          { icon: Calendar, label: "更新", value: project.lastUpdate || "Recently", color: "text-purple-400" },
        ].map((stat) => (
          <div
            key={stat.label}
            className="p-4 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md text-center"
          >
            <stat.icon className={`w-5 h-5 mx-auto mb-2 ${stat.color}`} />
            <div className="text-lg font-semibold text-foreground">{stat.value}</div>
            <div className="text-xs text-muted-foreground">{stat.label}</div>
          </div>
        ))}
      </motion.div>

      {/* Description */}
      <motion.section variants={itemVariants} className="mb-8">
        <h2 className="text-xl font-semibold text-foreground mb-4 flex items-center gap-2">
          <span className="w-1.5 h-1.5 rounded-full bg-primary" />
          项目介绍
        </h2>
        <div className="p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md">
          <p className="text-muted-foreground leading-relaxed whitespace-pre-line">
            {project.longDescription || project.description}
          </p>
        </div>
      </motion.section>

      {/* Features */}
      {project.features && (
        <motion.section variants={itemVariants} className="mb-8">
          <h2 className="text-xl font-semibold text-foreground mb-4 flex items-center gap-2">
            <span className="w-1.5 h-1.5 rounded-full bg-primary" />
            核心功能
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {project.features.map((feature, index) => (
              <div
                key={index}
                className="flex items-start gap-3 p-4 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md"
              >
                <span className="text-primary mt-0.5">✓</span>
                <span className="text-muted-foreground text-sm">{feature}</span>
              </div>
            ))}
          </div>
        </motion.section>
      )}

      {/* Tech Stack */}
      <motion.section variants={itemVariants} className="mb-8">
        <h2 className="text-xl font-semibold text-foreground mb-4 flex items-center gap-2">
          <span className="w-1.5 h-1.5 rounded-full bg-primary" />
          技术栈
        </h2>
        <div className="flex flex-wrap gap-2 mb-4">
          {project.tags.map((tag) => (
            <span
              key={tag}
              className="px-3 py-1.5 text-sm rounded-lg bg-zinc-800 text-zinc-300 font-mono"
            >
              {tag}
            </span>
          ))}
        </div>
        {project.techStack && (
          <div className="space-y-3">
            {project.techStack.map((tech) => (
              <div
                key={tech.name}
                className="p-4 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md"
              >
                <h3 className="font-medium text-foreground mb-1">{tech.name}</h3>
                <p className="text-sm text-muted-foreground">{tech.description}</p>
              </div>
            ))}
          </div>
        )}
      </motion.section>
    </motion.div>
  )
}
