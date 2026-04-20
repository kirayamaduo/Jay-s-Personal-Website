import { Link } from "react-router-dom"
import { motion } from "framer-motion"
import { Home, Terminal } from "lucide-react"

export default function NotFound() {
    return (
        <div className="min-h-[80vh] flex flex-col items-center justify-center text-center px-4 relative overflow-hidden">
            {/* 背景光效 */}
            <div className="absolute inset-0 bg-grid-white/[0.02] -z-10" />
            <div className="absolute h-[40rem] w-[40rem] bg-primary/20 rounded-full blur-[128px] -z-10 opacity-20 animate-pulse" />

            <motion.div
                initial={{ scale: 0.8, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                transition={{ duration: 0.5 }}
            >
                <h1 className="text-9xl font-bold text-transparent bg-clip-text bg-gradient-to-b from-white to-white/10 font-mono tracking-tighter">
                    404
                </h1>
            </motion.div>

            <motion.div
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 0.2, duration: 0.5 }}
                className="relative z-10"
            >
                <div className="text-2xl font-semibold text-foreground mt-4 mb-2">
                    信号丢失 (Signal Lost)
                </div>
                <p className="text-muted-foreground max-w-md mx-auto mb-8">
                    你似乎来到了未知的宇宙象限。这个坐标没有任何数据，或者已经被黑洞吞噬。
                </p>

                <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
                    <Link
                        to="/"
                        className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-primary text-primary-foreground font-medium hover:bg-primary/90 transition-all hover:scale-105"
                    >
                        <Home className="w-4 h-4" />
                        返回地球 (Home)
                    </Link>

                    <button
                        onClick={() => window.history.back()}
                        className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-white/5 border border-white/10 text-foreground font-medium hover:bg-white/10 transition-colors"
                    >
                        <Terminal className="w-4 h-4" />
                        执行撤退 (Back)
                    </button>
                </div>
            </motion.div>
        </div>
    )
}
