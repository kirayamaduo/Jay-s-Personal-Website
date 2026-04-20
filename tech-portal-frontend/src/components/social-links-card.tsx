import { Github, Linkedin, Twitter, Globe } from "lucide-react"

const links = [
  { name: "GitHub", icon: Github, href: "https://github.com", color: "hover:text-foreground" },
  { name: "LinkedIn", icon: Linkedin, href: "https://linkedin.com", color: "hover:text-[#0A66C2]" },
  { name: "Twitter", icon: Twitter, href: "https://twitter.com", color: "hover:text-[#1DA1F2]" },
  { name: "Website", icon: Globe, href: "#", color: "hover:text-primary" },
]

export function SocialLinksCard() {
  return (
    <div className="glass rounded-2xl p-6 h-full flex flex-col justify-center group hover:border-primary/30 transition-colors duration-300">
      <h3 className="text-lg font-semibold text-foreground mb-4 flex items-center gap-2">
        <span className="w-2 h-2 rounded-full bg-primary" />
        Connect
      </h3>
      <div className="grid grid-cols-2 gap-3">
        {links.map((link) => (
          <a
            key={link.name}
            href={link.href}
            target="_blank"
            rel="noopener noreferrer"
            className={`flex items-center gap-2 p-3 rounded-xl bg-secondary/30 text-muted-foreground ${link.color} transition-all duration-300 hover:bg-secondary/50`}
          >
            <link.icon className="w-4 h-4" />
            <span className="text-sm font-medium">{link.name}</span>
          </a>
        ))}
      </div>
    </div>
  )
}
