import { MapPin } from "lucide-react"

export function AboutCard() {
  return (
    <div className="glass rounded-2xl p-6 h-full flex flex-col justify-between group hover:border-primary/30 transition-colors duration-300">
      <div>
        <h3 className="text-lg font-semibold text-foreground mb-3 flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-primary" />
          About
        </h3>
        <p className="text-muted-foreground text-sm leading-relaxed">
          Crafting interfaces and building polished software experiences. Currently focused on building scalable web
          applications and exploring the intersection of design and engineering.
        </p>
      </div>
      <div className="flex items-center gap-2 mt-4 text-muted-foreground text-sm">
        <MapPin className="w-4 h-4 text-primary" />
        <span>San Francisco, CA</span>
      </div>
    </div>
  )
}
