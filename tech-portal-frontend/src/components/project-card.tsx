import { ArrowUpRight } from "lucide-react"

interface ProjectCardProps {
  title: string
  description: string
  tags: string[]
  link: string
}

export function ProjectCard({ title, description, tags, link }: ProjectCardProps) {
  return (
    <a
      href={link}
      target="_blank"
      rel="noopener noreferrer"
      className="glass rounded-2xl p-6 h-full flex flex-col justify-between group hover:border-primary/30 transition-all duration-300 block"
    >
      <div>
        <div className="flex items-start justify-between mb-3">
          <h3 className="text-lg font-semibold text-foreground group-hover:text-primary transition-colors">{title}</h3>
          <ArrowUpRight className="w-5 h-5 text-muted-foreground group-hover:text-primary group-hover:translate-x-1 group-hover:-translate-y-1 transition-all duration-300" />
        </div>
        <p className="text-muted-foreground text-sm leading-relaxed mb-4">{description}</p>
      </div>
      <div className="flex flex-wrap gap-2">
        {tags.map((tag) => (
          <span key={tag} className="text-xs px-2 py-1 rounded-md bg-primary/10 text-primary font-mono">
            {tag}
          </span>
        ))}
      </div>
    </a>
  )
}
