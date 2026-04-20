import { motion } from "framer-motion"
import { Code, Moon, Gamepad2, Coffee, Plane } from "lucide-react"

type Status = "coding" | "sleeping" | "gaming" | "coffee" | "traveling"

interface StatusConfig {
  icon: typeof Code
  label: string
  color: string
  bgColor: string
}

const statusConfig: Record<Status, StatusConfig> = {
  coding: {
    icon: Code,
    label: "正在写代码",
    color: "text-green-400",
    bgColor: "bg-green-400",
  },
  sleeping: {
    icon: Moon,
    label: "睡觉中...",
    color: "text-blue-400",
    bgColor: "bg-blue-400",
  },
  gaming: {
    icon: Gamepad2,
    label: "打游戏中",
    color: "text-purple-400",
    bgColor: "bg-purple-400",
  },
  coffee: {
    icon: Coffee,
    label: "喝咖啡摸鱼",
    color: "text-amber-400",
    bgColor: "bg-amber-400",
  },
  traveling: {
    icon: Plane,
    label: "在路上",
    color: "text-cyan-400",
    bgColor: "bg-cyan-400",
  },
}

interface StatusIndicatorProps {
  status?: Status
}

export function StatusIndicator({ status = "coding" }: StatusIndicatorProps) {
  const config = statusConfig[status]
  const Icon = config.icon

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="p-4 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md"
    >
      <div className="flex items-center gap-3">
        {/* Status Icon with Breathing Effect */}
        <div className="relative">
          <div
            className={`w-10 h-10 rounded-full bg-white/5 flex items-center justify-center ${config.color}`}
          >
            <Icon className="w-5 h-5" />
          </div>
          {/* Breathing Glow */}
          <motion.div
            className={`absolute inset-0 rounded-full ${config.bgColor} opacity-30`}
            animate={{
              scale: [1, 1.5, 1],
              opacity: [0.3, 0, 0.3],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: "easeInOut",
            }}
          />
        </div>

        {/* Status Text */}
        <div>
          <div className="flex items-center gap-2">
            <span className="text-xs text-muted-foreground">当前状态</span>
            {/* Online Dot */}
            <span className="relative flex h-2 w-2">
              <span
                className={`animate-ping absolute inline-flex h-full w-full rounded-full ${config.bgColor} opacity-75`}
              />
              <span
                className={`relative inline-flex rounded-full h-2 w-2 ${config.bgColor}`}
              />
            </span>
          </div>
          <p className={`text-sm font-medium ${config.color}`}>{config.label}</p>
        </div>
      </div>
    </motion.div>
  )
}
