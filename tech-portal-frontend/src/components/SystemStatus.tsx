import { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Activity, Cpu, Database, Wifi, Server } from "lucide-react"

export function SystemStatus() {
    const [stats, setStats] = useState({
        cpu: 12,
        memory: 24,
        latency: 45,
        status: "online"
    })

    const [isExpanded, setIsExpanded] = useState(false)

    useEffect(() => {
        const interval = setInterval(() => {
            setStats(prev => ({
                cpu: Math.min(100, Math.max(5, prev.cpu + (Math.random() * 10 - 5))),
                memory: Math.min(100, Math.max(10, prev.memory + (Math.random() * 5 - 2))),
                latency: Math.max(20, prev.latency + (Math.random() * 10 - 5)),
                status: "online"
            }))
        }, 2000)

        return () => clearInterval(interval)
    }, [])

    return (
        <motion.div
            className="fixed bottom-6 left-6 z-40 hidden md:block"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
        >
            <motion.div
                className="bg-black/80 border border-white/10 backdrop-blur-md rounded-2xl overflow-hidden cursor-pointer hover:border-primary/30 transition-colors"
                onClick={() => setIsExpanded(!isExpanded)}
                animate={{ width: isExpanded ? "auto" : "48px", height: isExpanded ? "auto" : "48px" }}
                transition={{ type: "spring", stiffness: 300, damping: 25 }}
            >
                <div className={`flex items-center ${isExpanded ? "p-4 gap-6" : "p-3 justify-center h-full"}`}>

                    {/* Collapsed Icon */}
                    {!isExpanded && (
                        <Activity className="w-5 h-5 text-green-500 animate-pulse" />
                    )}

                    {/* Expanded Content */}
                    <AnimatePresence>
                        {isExpanded && (
                            <motion.div
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                exit={{ opacity: 0 }}
                                className="flex items-center gap-6 whitespace-nowrap"
                            >
                                {/* System Status */}
                                <div className="flex items-center gap-2">
                                    <div className="relative">
                                        <Server className="w-4 h-4 text-primary" />
                                        <span className="absolute -top-1 -right-1 w-2 h-2 bg-green-500 rounded-full animate-pulse" />
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="text-xs text-muted-foreground">System</span>
                                        <span className="text-xs font-mono text-green-400">ONLINE</span>
                                    </div>
                                </div>

                                {/* CPU */}
                                <div className="flex items-center gap-2">
                                    <Cpu className="w-4 h-4 text-blue-400" />
                                    <div className="flex flex-col w-16">
                                        <span className="text-xs text-muted-foreground">CPU</span>
                                        <div className="h-1.5 w-full bg-white/10 rounded-full mt-1 overflow-hidden">
                                            <motion.div
                                                className="h-full bg-blue-500"
                                                animate={{ width: `${stats.cpu}%` }}
                                            />
                                        </div>
                                    </div>
                                    <span className="text-xs font-mono w-6">{Math.round(stats.cpu)}%</span>
                                </div>

                                {/* Memory */}
                                <div className="flex items-center gap-2">
                                    <Database className="w-4 h-4 text-purple-400" />
                                    <div className="flex flex-col w-16">
                                        <span className="text-xs text-muted-foreground">RAM</span>
                                        <div className="h-1.5 w-full bg-white/10 rounded-full mt-1 overflow-hidden">
                                            <motion.div
                                                className="h-full bg-purple-500"
                                                animate={{ width: `${stats.memory}%` }}
                                            />
                                        </div>
                                    </div>
                                    <span className="text-xs font-mono w-6">{Math.round(stats.memory)}%</span>
                                </div>

                                {/* Network */}
                                <div className="flex items-center gap-2">
                                    <Wifi className="w-4 h-4 text-orange-400" />
                                    <div className="flex flex-col">
                                        <span className="text-xs text-muted-foreground">Ping</span>
                                        <span className="text-xs font-mono text-orange-300">{Math.round(stats.latency)}ms</span>
                                    </div>
                                </div>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>
            </motion.div>
        </motion.div>
    )
}
