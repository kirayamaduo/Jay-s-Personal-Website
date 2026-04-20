import { GitCommit, GitPullRequest, Star, Users } from "lucide-react"

const stats = [
  { label: "Contributions", value: "2,847", icon: GitCommit },
  { label: "Pull Requests", value: "156", icon: GitPullRequest },
  { label: "Stars Earned", value: "892", icon: Star },
  { label: "Followers", value: "1.2k", icon: Users },
]

export function GithubStatsCard() {
  return (
    <div className="glass rounded-2xl p-6 h-full group hover:border-primary/30 transition-colors duration-300">
      <h3 className="text-lg font-semibold text-foreground mb-4 flex items-center gap-2">
        <span className="w-2 h-2 rounded-full bg-primary" />
        GitHub Stats
      </h3>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {stats.map((stat) => (
          <div
            key={stat.label}
            className="text-center p-3 rounded-xl bg-secondary/30 hover:bg-primary/10 transition-colors duration-300"
          >
            <stat.icon className="w-5 h-5 mx-auto mb-2 text-primary" />
            <p className="text-xl font-bold text-foreground">{stat.value}</p>
            <p className="text-xs text-muted-foreground">{stat.label}</p>
          </div>
        ))}
      </div>

      {/* Activity graph placeholder */}
      <div className="mt-4 flex items-end gap-1 h-16 justify-center">
        {Array.from({ length: 52 }).map((_, i) => (
          <div
            key={i}
            className="w-2 rounded-sm bg-primary/30 hover:bg-primary transition-colors duration-200"
            style={{
              height: `${Math.random() * 100}%`,
              opacity: 0.3 + Math.random() * 0.7,
            }}
          />
        ))}
      </div>
    </div>
  )
}
