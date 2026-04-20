import { Navbar } from "@/components/Navbar"
import { Footer } from "@/components/Footer"
import { Toaster } from "@/components/ui/toaster"
import { AnimatedOutlet } from "@/components/AnimatedOutlet"
import { Terminal } from "@/components/Terminal"
import { SystemStatus } from "@/components/SystemStatus"

export default function MainLayout() {
  return (
    <div className="min-h-screen bg-background">
      {/* 动态背景光晕 */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-primary/20 rounded-full blur-[100px] opacity-50" />
        <div className="absolute top-1/2 -left-40 w-96 h-96 bg-primary/10 rounded-full blur-[120px] opacity-30" />
        <div className="absolute -bottom-40 right-1/3 w-72 h-72 bg-purple-500/10 rounded-full blur-[100px] opacity-40" />
      </div>

      {/* Components */}
      <Terminal />
      <SystemStatus />

      {/* Navbar */}
      <Navbar />

      {/* Main Content */}
      <main className="relative z-10 pt-24 pb-16 px-4 md:px-8">
        <AnimatedOutlet />
      </main>

      {/* Footer */}
      <Footer />

      {/* Toast Notifications */}
      <Toaster />
    </div>
  )
}
