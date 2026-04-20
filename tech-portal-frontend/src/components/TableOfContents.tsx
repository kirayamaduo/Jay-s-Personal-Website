import { useState, useEffect } from "react"
import { Link } from "lucide-react"

interface TableOfContentsProps {
  content: string
}

interface TocItem {
  id: string
  text: string
  level: number
}

export function TableOfContents({ content }: TableOfContentsProps) {
  const [headings, setHeadings] = useState<TocItem[]>([])
  const [activeId, setActiveId] = useState<string>("")

  useEffect(() => {
    // Parse markdown content to extract h2 and h3
    const lines = content.split("\n")
    const extractedHeadings: TocItem[] = []

    lines.forEach((line) => {
      const match = line.match(/^(#{2,3})\s+(.+)$/)
      if (match) {
        const level = match[1].length
        const text = match[2].trim()
        // Create a specialized ID that matches how rehype-slug works usually (simple lowercase-dash)
        // Note: Ideally we should use the exact same slugifier as rehype-slug, but simple one works for 90% cases
        const id = text
          .toLowerCase()
          .replace(/\s+/g, "-")
          .replace(/[^\w\u4e00-\u9fa5-]/g, "") // Keep Chinese chars

        extractedHeadings.push({ id, text, level })
      }
    })

    setHeadings(extractedHeadings)
  }, [content])

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setActiveId(entry.target.id)
          }
        })
      },
      { rootMargin: "-100px 0px -66% 0px" }
    )

    headings.forEach((heading) => {
      const element = document.getElementById(heading.id)
      if (element) {
        observer.observe(element)
      }
    })

    return () => observer.disconnect()
  }, [headings])

  if (headings.length === 0) return null

  return (
    <nav className="sticky top-24 max-h-[calc(100vh-120px)] overflow-y-auto pr-4 hidden xl:block w-64">
      <div className="flex items-center gap-2 mb-4 text-foreground font-semibold">
        <Link className="w-4 h-4" />
        <span>目录</span>
      </div>
      <div className="relative border-l border-white/10 pl-4 space-y-3">
        {/* Active Indicator Line */}
        {/* Simplified approach: just style the active link differently, 
            or we could add a moving indicator if we knew exact heights. 
            For now, border-l-primary on active item is easiest. */}

        {headings.map((heading) => (
          <a
            key={heading.id}
            href={`#${heading.id}`}
            onClick={(e) => {
              e.preventDefault()
              document.getElementById(heading.id)?.scrollIntoView({
                behavior: "smooth",
              })
              setActiveId(heading.id)
            }}
            className={`block text-sm transition-colors duration-200 border-l-2 -ml-[17px] pl-4 ${activeId === heading.id
              ? "border-primary text-primary font-medium"
              : "border-transparent text-muted-foreground hover:text-foreground"
              } ${heading.level === 3 ? "pl-8" : ""}`}
          >
            {heading.text}
          </a>
        ))}
      </div>
    </nav>
  )
}
