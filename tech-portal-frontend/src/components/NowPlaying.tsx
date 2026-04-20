import { motion } from "framer-motion"
import { Music, Pause, Play } from "lucide-react"
import { useState } from "react"

interface NowPlayingProps {
  song?: string
  artist?: string
  isPlaying?: boolean
}

// 音频可视化条形图
function AudioBars({ isPlaying }: { isPlaying: boolean }) {
  return (
    <div className="flex items-end gap-[2px] h-4">
      {[0, 1, 2, 3].map((i) => (
        <motion.div
          key={i}
          className="w-[3px] bg-primary rounded-full"
          animate={
            isPlaying
              ? {
                  height: ["40%", "100%", "60%", "80%", "40%"],
                }
              : { height: "40%" }
          }
          transition={
            isPlaying
              ? {
                  duration: 0.8,
                  repeat: Infinity,
                  repeatType: "reverse",
                  delay: i * 0.1,
                  ease: "easeInOut",
                }
              : {}
          }
        />
      ))}
    </div>
  )
}

export function NowPlaying({
  song = "Midnight City",
  artist = "M83",
  isPlaying: initialPlaying = true,
}: NowPlayingProps) {
  const [isPlaying, setIsPlaying] = useState(initialPlaying)

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="p-4 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md"
    >
      <div className="flex items-center gap-3">
        {/* Album Art Placeholder */}
        <div className="w-12 h-12 rounded-lg bg-gradient-to-br from-primary/60 to-purple-500/60 flex items-center justify-center shrink-0">
          <Music className="w-6 h-6 text-white/80" />
        </div>

        {/* Song Info */}
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs text-primary font-medium">正在听</span>
            <AudioBars isPlaying={isPlaying} />
          </div>
          <p className="text-sm font-medium text-foreground truncate">{song}</p>
          <p className="text-xs text-muted-foreground truncate">{artist}</p>
        </div>

        {/* Play/Pause Button */}
        <button
          onClick={() => setIsPlaying(!isPlaying)}
          className="p-2 rounded-full bg-white/5 hover:bg-white/10 transition-colors"
        >
          {isPlaying ? (
            <Pause className="w-4 h-4 text-foreground" />
          ) : (
            <Play className="w-4 h-4 text-foreground" />
          )}
        </button>
      </div>

      {/* Progress Bar */}
      <div className="mt-3 h-1 bg-white/10 rounded-full overflow-hidden">
        <motion.div
          className="h-full bg-primary rounded-full"
          initial={{ width: "30%" }}
          animate={isPlaying ? { width: ["30%", "100%"] } : {}}
          transition={
            isPlaying
              ? { duration: 180, ease: "linear", repeat: Infinity }
              : {}
          }
        />
      </div>
    </motion.div>
  )
}
