import { Badge } from "@/components/ui/badge"

const technologies = [
  { name: "React", icon: "⚛️" },
  { name: "Next.js", icon: "▲" },
  { name: "TypeScript", icon: "📘" },
  { name: "Node.js", icon: "💚" },
  { name: "Python", icon: "🐍" },
  { name: "PostgreSQL", icon: "🐘" },
  { name: "AWS", icon: "☁️" },
  { name: "Docker", icon: "🐳" },
  { name: "GraphQL", icon: "◈" },
  { name: "Tailwind", icon: "🎨" },
]

export function TechStackCard() {
  return (
    <div className="glass rounded-2xl p-6 h-full group hover:border-primary/30 transition-colors duration-300">
      <h3 className="text-lg font-semibold text-foreground mb-4 flex items-center gap-2">
        <span className="w-2 h-2 rounded-full bg-primary" />
        Tech Stack
      </h3>
      <div className="flex flex-wrap gap-2">
        {technologies.map((tech) => (
          <Badge
            key={tech.name}
            variant="secondary"
            className="flex items-center gap-2 px-3 py-2 cursor-default"
          >
            <span>{tech.icon}</span>
            <span className="text-sm font-medium">{tech.name}</span>
          </Badge>
        ))}
      </div>
    </div>
  )
}
