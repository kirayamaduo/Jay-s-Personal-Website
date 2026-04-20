import { Github, Linkedin, Twitter } from "lucide-react"

export function HeroCard() {
  return (
    <div className="glass rounded-2xl p-8 h-full flex flex-col justify-between relative overflow-hidden group">
      {/* Decorative gradient orb */}
      <div className="absolute -top-20 -right-20 w-64 h-64 bg-primary/20 rounded-full blur-3xl opacity-50 group-hover:opacity-70 transition-opacity duration-500" />

      <div className="relative z-10">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary/80 to-primary/40 flex items-center justify-center text-3xl font-bold text-primary-foreground">
            AC
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-mono">Hello, I'm</p>
            <h1 className="text-3xl md:text-4xl font-bold text-foreground">Alex Chen</h1>
          </div>
        </div>

        <h2 className="text-xl md:text-2xl font-semibold text-primary mb-4">Full Stack Developer</h2>

        <p className="text-muted-foreground leading-relaxed max-w-md">
          I build accessible, pixel-perfect digital experiences for the web. Passionate about creating elegant solutions
          to complex problems.
        </p>
      </div>

      <div className="relative z-10 flex items-center gap-4 mt-6">
        <a
          href="https://github.com"
          target="_blank"
          rel="noopener noreferrer"
          className="p-3 rounded-xl bg-secondary/50 hover:bg-primary/20 hover:text-primary transition-all duration-300"
        >
          <Github className="w-5 h-5" />
        </a>
        <a
          href="https://linkedin.com"
          target="_blank"
          rel="noopener noreferrer"
          className="p-3 rounded-xl bg-secondary/50 hover:bg-primary/20 hover:text-primary transition-all duration-300"
        >
          <Linkedin className="w-5 h-5" />
        </a>
        <a
          href="https://twitter.com"
          target="_blank"
          rel="noopener noreferrer"
          className="p-3 rounded-xl bg-secondary/50 hover:bg-primary/20 hover:text-primary transition-all duration-300"
        >
          <Twitter className="w-5 h-5" />
        </a>
      </div>
    </div>
  )
}
