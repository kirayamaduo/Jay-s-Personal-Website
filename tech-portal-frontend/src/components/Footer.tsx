import { Github, Twitter, Mail } from "lucide-react"

export function Footer() {
  const currentYear = new Date().getFullYear()

  return (
    <footer className="relative z-10 border-t border-white/10 bg-black/20 backdrop-blur-sm">
      <div className="mx-auto max-w-7xl px-4 md:px-8 py-8">
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          {/* Copyright */}
          <div className="text-sm text-muted-foreground">
            <span>© {currentYear} Alex Chen. </span>
            <span className="hidden sm:inline">Built with React & Tailwind CSS</span>
          </div>

          {/* Social Links */}
          <div className="flex items-center gap-4">
            <a
              href="https://github.com"
              target="_blank"
              rel="noopener noreferrer"
              className="p-2 rounded-lg text-muted-foreground hover:text-foreground hover:bg-white/5 transition-colors"
            >
              <Github className="w-5 h-5" />
            </a>
            <a
              href="https://twitter.com"
              target="_blank"
              rel="noopener noreferrer"
              className="p-2 rounded-lg text-muted-foreground hover:text-foreground hover:bg-white/5 transition-colors"
            >
              <Twitter className="w-5 h-5" />
            </a>
            <a
              href="mailto:hello@example.com"
              className="p-2 rounded-lg text-muted-foreground hover:text-foreground hover:bg-white/5 transition-colors"
            >
              <Mail className="w-5 h-5" />
            </a>
          </div>
        </div>
      </div>
    </footer>
  )
}
