import { Briefcase } from "lucide-react"

export function ExperienceCard() {
  return (
    <div className="glass rounded-2xl p-6 h-full flex flex-col justify-between group hover:border-primary/30 transition-colors duration-300">
      <div>
        <h3 className="text-lg font-semibold text-foreground mb-3 flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-primary" />
          Experience
        </h3>
        <div className="space-y-3">
          <div className="flex items-start gap-3">
            <Briefcase className="w-4 h-4 text-primary mt-1 shrink-0" />
            <div>
              <p className="text-sm font-medium text-foreground">Senior Developer</p>
              <p className="text-xs text-muted-foreground">TechCorp • 2022 - Present</p>
            </div>
          </div>
          <div className="flex items-start gap-3">
            <Briefcase className="w-4 h-4 text-muted-foreground mt-1 shrink-0" />
            <div>
              <p className="text-sm font-medium text-foreground">Full Stack Developer</p>
              <p className="text-xs text-muted-foreground">StartupXYZ • 2020 - 2022</p>
            </div>
          </div>
        </div>
      </div>
      <p className="text-xs text-muted-foreground mt-4">5+ years of experience</p>
    </div>
  )
}
