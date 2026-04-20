import { useState, useEffect, useRef } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { X, Terminal as TerminalIcon, Maximize2, Minimize2 } from "lucide-react"
import { usePersonalData, useProjects, useBlogPosts } from "@/hooks/useData"

interface Command {
    command: string
    output: React.ReactNode
}

export function Terminal() {
    const [isOpen, setIsOpen] = useState(false)
    const [isMaximized, setIsMaximized] = useState(false)
    const [input, setInput] = useState("")
    const [history, setHistory] = useState<Command[]>([
        { command: "help", output: "Type 'help' to see available commands." }
    ])
    const [historyIndex, setHistoryIndex] = useState(-1)
    const [commandHistory, setCommandHistory] = useState<string[]>([])

    const inputRef = useRef<HTMLInputElement>(null)
    const bottomRef = useRef<HTMLDivElement>(null)

    const { experiences, socialLinks } = usePersonalData()
    const { projects } = useProjects()
    const { posts } = useBlogPosts()

    // Scroll to bottom on new output
    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" })
    }, [history, isOpen])

    // Focus input on click
    useEffect(() => {
        if (isOpen) {
            inputRef.current?.focus()
        }
    }, [isOpen])

    // Keyboard shortcut to toggle terminal
    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if ((e.metaKey || e.ctrlKey) && e.key === "k") {
                e.preventDefault()
                setIsOpen((prev) => !prev)
            }
        }
        window.addEventListener("keydown", handleKeyDown)
        return () => window.removeEventListener("keydown", handleKeyDown)
    }, [])

    // Typewriter effect on mount
    useEffect(() => {
        const initialCommand = "npm run dev"
        let i = 0
        const interval = setInterval(() => {
            if (i < initialCommand.length) {
                setInput(prev => prev + initialCommand.charAt(i))
                i++
            } else {
                clearInterval(interval)
                setTimeout(() => {
                    setHistory(prev => [...prev, { command: initialCommand, output: <span className="text-green-400">Successfully started development server...</span> }])
                    setInput("")
                }, 400) // Delay before "executing"
            }
        }, 100) // Typing speed
        return () => clearInterval(interval)
    }, [])

    const handleCommand = (cmd: string) => {
        if (!cmd.trim()) return

        const args = cmd.trim().toLowerCase().split(" ")
        const command = args[0]
        let output: React.ReactNode = ""

        switch (command) {
            case "help":
                output = (
                    <div className="flex flex-col gap-1 text-primary">
                        <div>Available commands:</div>
                        <div className="grid grid-cols-[100px_1fr] gap-2 pl-4">
                            <span>about</span>
                            <span className="text-muted-foreground">Show profile information</span>
                            <span>skills</span>
                            <span className="text-muted-foreground">List technical skills</span>
                            <span>projects</span>
                            <span className="text-muted-foreground">List featured projects</span>
                            <span>blog</span>
                            <span className="text-muted-foreground">List latest blog posts</span>
                            <span>social</span>
                            <span className="text-muted-foreground">Show social links</span>
                            <span>contact</span>
                            <span className="text-muted-foreground">Show contact information</span>
                            <span>clear</span>
                            <span className="text-muted-foreground">Clear terminal history</span>
                            <span>gui</span>
                            <span className="text-muted-foreground">Open current view in GUI mode (navigation)</span>
                        </div>
                    </div>
                )
                break
            case "clear":
                setHistory([])
                return
            case "about":
                const currentJob = experiences.find(e => e.type === "work")
                output = (
                    <div className="text-zinc-300">
                        <div className="font-bold text-primary mb-2">Hello, I'm Lin! 👋</div>
                        <p>I am a Full Stack Developer based in {currentJob?.location || "San Francisco"}.</p>
                        <p className="mt-1">Currently working at <span className="text-zinc-100">{currentJob?.company || "Tech Corp"}</span>.</p>
                        <p className="mt-2">Core Tech Stack:</p>
                        <ul className="list-disc list-inside pl-2 mt-1 mb-2 text-muted-foreground">
                            <li>Frontend: React, TypeScript, Tailwind CSS</li>
                            <li>Backend: Spring Boot, Java, Redis</li>
                            <li>Database: MySQL, PostgreSQL</li>
                        </ul>
                    </div>
                )
                break
            case "skills":
                // This assumes personalData might eventually have skills, for now hardcoding or deriving
                output = (
                    <div className="flex flex-wrap gap-2 text-zinc-300">
                        <span className="text-primary">Frontend:</span> React, Vue, TypeScript, Tailwind CSS, Next.js
                        <br />
                        <span className="text-primary">Backend:</span> Java, Spring Boot, Node.js, Python, FastAPI
                        <br />
                        <span className="text-primary">DevOps:</span> Docker, Kubernetes, AWS, CI/CD
                    </div>
                )
                break
            case "projects":
                output = (
                    <div className="flex flex-col gap-2">
                        <div className="text-primary mb-1">Featured Projects:</div>
                        {projects.filter(p => p.featured).map(p => (
                            <div key={p.id}>
                                <span className="font-bold text-zinc-200">{p.title}</span> - <span className="text-muted-foreground">{p.description}</span>
                                <div className="text-xs text-zinc-500 pl-4">Stack: {p.tags.join(", ")}</div>
                            </div>
                        ))}
                        <div className="mt-2 text-xs text-muted-foreground">Type `cd projects` to navigate to projects page.</div>
                    </div>
                )
                break
            case "blog":
                output = (
                    <div className="flex flex-col gap-2">
                        <div className="text-primary mb-1">Latest Posts:</div>
                        {posts.slice(0, 3).map(p => (
                            <div key={p.id}>
                                <span className="text-zinc-400">[{p.date}]</span> <span className="text-zinc-200">{p.title}</span>
                            </div>
                        ))}
                    </div>
                )
                break
            case "social":
                output = (
                    <div className="flex flex-col gap-1">
                        {socialLinks.map(link => (
                            <div key={link.label}>
                                <span className="w-24 inline-block text-primary">{link.label}:</span>
                                <a href={link.href} target="_blank" rel="noopener noreferrer" className="text-blue-400 hover:underline">{link.username}</a>
                            </div>
                        ))}
                    </div>
                )
                break
            case "contact":
                output = "You can reach me at hello@example.com"
                break
            case "gui":
                // Simple navigation simulation output
                // In real app we might use window.location or router
                output = "Navigating to GUI..."
                break
            case "sudo":
                output = <span className="text-red-500 font-bold">Permission denied: You are not authorized to run sudo. Nice try! 😉</span>
                break
            default:
                output = <span className="text-red-400">Command not found: {command}. Type 'help' for available commands.</span>
        }

        setHistory(prev => [...prev, { command: cmd, output }])
        setCommandHistory(prev => [...prev, cmd])
        setHistoryIndex(-1)
    }

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === "Enter") {
            handleCommand(input)
            setInput("")
        } else if (e.key === "ArrowUp") {
            e.preventDefault()
            if (historyIndex < commandHistory.length - 1) {
                const newIndex = historyIndex + 1
                setHistoryIndex(newIndex)
                setInput(commandHistory[commandHistory.length - 1 - newIndex])
            }
        } else if (e.key === "ArrowDown") {
            e.preventDefault()
            if (historyIndex > 0) {
                const newIndex = historyIndex - 1
                setHistoryIndex(newIndex)
                setInput(commandHistory[commandHistory.length - 1 - newIndex])
            } else if (historyIndex === 0) {
                setHistoryIndex(-1)
                setInput("")
            }
        }
    }

    return (
        <>
            {/* Toggle Button (Fixed) */}
            <motion.button
                className="fixed bottom-6 right-6 z-50 p-3 rounded-full bg-black/80 border border-white/20 text-white shadow-lg backdrop-blur-md hover:border-primary/50 transition-colors"
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                onClick={() => setIsOpen(true)}
            >
                <TerminalIcon className="w-6 h-6" />
            </motion.button>

            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, y: 20, scale: 0.95 }}
                        animate={{
                            opacity: 1,
                            y: 0,
                            scale: 1,
                            width: isMaximized ? "100vw" : "min(800px, 90vw)",
                            height: isMaximized ? "100vh" : "min(600px, 80vh)",
                            borderRadius: isMaximized ? 0 : "1rem",
                            top: isMaximized ? 0 : "10%",
                            left: isMaximized ? 0 : "50%",
                            x: isMaximized ? 0 : "-50%",
                        }}
                        exit={{ opacity: 0, y: 20, scale: 0.95 }}
                        transition={{ type: "spring", damping: 25, stiffness: 300 }}
                        className="fixed z-[100] bg-black/90 border border-white/10 shadow-2xl backdrop-blur-xl overflow-hidden flex flex-col font-mono"
                        style={{
                            // Reset fixed positioning styles not handled by animate
                            top: isMaximized ? 0 : "10%",
                            left: isMaximized ? 0 : "50%",
                        }}
                    >
                        {/* Title Bar */}
                        <div className="flex items-center justify-between px-4 py-2 bg-white/5 border-b border-white/5">
                            <div className="flex items-center gap-2">
                                <div className="flex gap-1.5">
                                    <div className="w-3 h-3 rounded-full bg-red-500 cursor-pointer hover:bg-red-400" onClick={() => setIsOpen(false)} />
                                    <div className="w-3 h-3 rounded-full bg-yellow-500 cursor-pointer hover:bg-yellow-400" onClick={() => setIsMaximized(!isMaximized)} />
                                    <div className="w-3 h-3 rounded-full bg-green-500 cursor-pointer hover:bg-green-400" />
                                </div>
                                <span className="ml-3 text-xs text-muted-foreground flex items-center gap-1">
                                    <TerminalIcon className="w-3 h-3" />
                                    guest@lin-tech-portal:~
                                </span>
                            </div>
                            <div className="flex items-center gap-2 text-muted-foreground">
                                <button onClick={() => setIsMaximized(!isMaximized)} className="hover:text-white transition-colors">
                                    {isMaximized ? <Minimize2 className="w-4 h-4" /> : <Maximize2 className="w-4 h-4" />}
                                </button>
                                <button onClick={() => setIsOpen(false)} className="hover:text-white transition-colors">
                                    <X className="w-4 h-4" />
                                </button>
                            </div>
                        </div>

                        {/* Content Area */}
                        <div className="flex-1 overflow-y-auto p-4 space-y-2 scrollbar-thin scrollbar-thumb-white/10 scrollbar-track-transparent" onClick={() => inputRef.current?.focus()}>
                            <div className="text-zinc-400 mb-4">
                                Welcome to Lin's Terminal v1.0.0
                                <br />
                                Type <span className="text-primary">'help'</span> to see available commands.
                                <br />
                                Press <span className="text-primary">Cmd+K</span> to toggle this terminal anywhere.
                            </div>

                            {history.map((item, i) => (
                                <div key={i} className="mb-2">
                                    <div className="flex items-center gap-2 text-zinc-400">
                                        <span className="text-green-500">➜</span>
                                        <span className="text-blue-400">~</span>
                                        <span className="opacity-50">$</span>
                                        <span className="text-zinc-100">{item.command}</span>
                                    </div>
                                    <div className="pl-6 mt-1 text-zinc-300 leading-relaxed">
                                        {item.output}
                                    </div>
                                </div>
                            ))}

                            <div className="flex items-center gap-2 text-zinc-400 pt-2">
                                <span className="text-green-500">➜</span>
                                <span className="text-blue-400">~</span>
                                <span className="opacity-50">$</span>
                                <input
                                    ref={inputRef}
                                    type="text"
                                    value={input}
                                    onChange={(e) => setInput(e.target.value)}
                                    onKeyDown={handleKeyDown}
                                    className="flex-1 bg-transparent border-none outline-none text-zinc-100 placeholder:text-zinc-600 focus:ring-0 p-0 transform-none"
                                    autoComplete="off"
                                    autoCorrect="off"
                                    autoCapitalize="off"
                                    spellCheck="false"
                                    autoFocus
                                />
                            </div>
                            <div ref={bottomRef} />
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </>
    )
}
