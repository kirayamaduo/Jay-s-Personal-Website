import { useEffect, useRef, useState } from "react"
import { motion } from "framer-motion"

interface Skill {
  name: string
  level: number // 1-5, affects size
}

const skills: Skill[] = [
  { name: "React", level: 5 },
  { name: "TypeScript", level: 5 },
  { name: "Node.js", level: 4 },
  { name: "Python", level: 4 },
  { name: "Next.js", level: 4 },
  { name: "Tailwind", level: 5 },
  { name: "PostgreSQL", level: 3 },
  { name: "Redis", level: 3 },
  { name: "Docker", level: 3 },
  { name: "AWS", level: 3 },
  { name: "GraphQL", level: 3 },
  { name: "Vue", level: 3 },
  { name: "Spring Boot", level: 4 },
  { name: "Git", level: 5 },
  { name: "Linux", level: 3 },
  { name: "MongoDB", level: 2 },
  { name: "Rust", level: 2 },
  { name: "Go", level: 2 },
]

interface Point3D {
  x: number
  y: number
  z: number
}

function fibonacciSphere(samples: number): Point3D[] {
  const points: Point3D[] = []
  const phi = Math.PI * (Math.sqrt(5) - 1) // golden angle

  for (let i = 0; i < samples; i++) {
    const y = 1 - (i / (samples - 1)) * 2
    const radius = Math.sqrt(1 - y * y)
    const theta = phi * i

    points.push({
      x: Math.cos(theta) * radius,
      y: y,
      z: Math.sin(theta) * radius,
    })
  }

  return points
}

export function SkillCloud() {
  const containerRef = useRef<HTMLDivElement>(null)
  const [rotation, setRotation] = useState({ x: 0, y: 0 })
  const [isHovered, setIsHovered] = useState(false)
  const animationRef = useRef<number>()

  const radius = 150
  const points = fibonacciSphere(skills.length)

  // Auto rotation
  useEffect(() => {
    let lastTime = Date.now()

    const animate = () => {
      const now = Date.now()
      const delta = (now - lastTime) / 1000
      lastTime = now

      if (!isHovered) {
        setRotation((prev) => ({
          x: prev.x,
          y: prev.y + delta * 0.3,
        }))
      }

      animationRef.current = requestAnimationFrame(animate)
    }

    animationRef.current = requestAnimationFrame(animate)

    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current)
      }
    }
  }, [isHovered])

  // Mouse interaction
  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!containerRef.current) return

    const rect = containerRef.current.getBoundingClientRect()
    const x = (e.clientX - rect.left - rect.width / 2) / rect.width
    const y = (e.clientY - rect.top - rect.height / 2) / rect.height

    setRotation({
      x: -y * Math.PI * 0.5,
      y: x * Math.PI * 0.5,
    })
  }

  const getTransformedPoint = (point: Point3D): { x: number; y: number; z: number; scale: number; opacity: number } => {
    // Rotate around Y axis
    const cosY = Math.cos(rotation.y)
    const sinY = Math.sin(rotation.y)
    let x = point.x * cosY - point.z * sinY
    let z = point.x * sinY + point.z * cosY

    // Rotate around X axis
    const cosX = Math.cos(rotation.x)
    const sinX = Math.sin(rotation.x)
    const y = point.y * cosX - z * sinX
    z = point.y * sinX + z * cosX

    // Perspective
    const perspective = 400
    const scale = perspective / (perspective + z * radius + radius)
    const opacity = (z + 1) / 2 * 0.7 + 0.3

    return {
      x: x * radius * scale,
      y: y * radius * scale,
      z,
      scale,
      opacity,
    }
  }

  return (
    <div
      ref={containerRef}
      className="relative w-full h-[350px] cursor-grab active:cursor-grabbing"
      onMouseMove={handleMouseMove}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div className="absolute inset-0 flex items-center justify-center">
        {skills.map((skill, index) => {
          const point = points[index]
          const transformed = getTransformedPoint(point)
          const size = 10 + skill.level * 2

          return (
            <motion.div
              key={skill.name}
              className="absolute"
              style={{
                transform: `translate(${transformed.x}px, ${transformed.y}px)`,
                zIndex: Math.round((transformed.z + 1) * 50),
              }}
              animate={{
                opacity: transformed.opacity,
                scale: transformed.scale,
              }}
              transition={{ duration: 0.1 }}
            >
              <span
                className="px-3 py-1.5 rounded-lg bg-zinc-800/80 text-zinc-300 font-mono whitespace-nowrap border border-white/5 hover:bg-primary/20 hover:text-primary hover:border-primary/30 transition-colors cursor-default"
                style={{ fontSize: `${size}px` }}
              >
                {skill.name}
              </span>
            </motion.div>
          )
        })}
      </div>

      {/* Center glow */}
      <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
        <div className="w-32 h-32 rounded-full bg-primary/10 blur-3xl" />
      </div>
    </div>
  )
}
