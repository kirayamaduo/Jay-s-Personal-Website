import { motion } from "framer-motion"
import { MapPin, Mail, Coffee } from "lucide-react"
import { usePersonalData } from "@/hooks/useData"

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.1 },
  },
}

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.5 } },
}

export default function About() {
  const { experiences, socialLinks, githubStats } = usePersonalData()

  return (
    <motion.div
      className="mx-auto max-w-4xl"
      variants={containerVariants}
      initial="hidden"
      animate="visible"
    >
      {/* Header */}
      <motion.div className="mb-16 text-center" variants={itemVariants}>
        <div className="w-32 h-32 mx-auto mb-6 rounded-full bg-gradient-to-r from-primary to-purple-500 p-1">
          <div className="w-full h-full rounded-full bg-black overflow-hidden">
            <img
              src="https://github.com/shadcn.png"
              alt="Profile"
              className="w-full h-full object-cover"
            />
          </div>
        </div>
        <h1 className="text-4xl font-bold text-foreground mb-4">关于我</h1>
        <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
          全栈开发者，开源爱好者。喜欢折腾新技术，探索代码的艺术。
          <span className="inline-block animate-bounce ml-2">🚀</span>
        </p>

        {/* Info badges */}
        <div className="flex flex-wrap justify-center gap-4 mt-6">
          <div className="flex items-center gap-2 px-4 py-2 rounded-full bg-white/5 border border-white/10 text-sm md:text-base">
            <MapPin className="w-4 h-4 text-primary" />
            <span>San Francisco, CA</span>
          </div>
          <div className="flex items-center gap-2 px-4 py-2 rounded-full bg-white/5 border border-white/10 text-sm md:text-base">
            <Mail className="w-4 h-4 text-primary" />
            <span>hello@example.com</span>
          </div>
          <div className="flex items-center gap-2 px-4 py-2 rounded-full bg-white/5 border border-white/10 text-sm md:text-base">
            <Coffee className="w-4 h-4 text-primary" />
            <span>Coffee Driven</span>
          </div>
        </div>
      </motion.div>

      {/* GitHub Stats */}
      <motion.div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-16" variants={itemVariants}>
        {githubStats.map((stat) => (
          <div
            key={stat.label}
            className="flex flex-col items-center p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-primary/30 transition-colors"
          >
            <stat.icon className="w-8 h-8 text-primary mb-3" />
            <span className="text-3xl font-bold text-foreground mb-1">{stat.value}</span>
            <span className="text-sm text-muted-foreground">{stat.label}</span>
          </div>
        ))}
      </motion.div>

      {/* Social Links */}
      <motion.div className="flex justify-center gap-6 mb-16" variants={itemVariants}>
        {socialLinks.map((link) => (
          <a
            key={link.label}
            href={link.href}
            target="_blank"
            rel="noopener noreferrer"
            className="group flex flex-col items-center gap-2"
          >
            <div className="p-4 rounded-xl bg-white/5 border border-white/10 group-hover:bg-primary/20 group-hover:border-primary/50 transition-all duration-300">
              <link.icon className="w-6 h-6 text-muted-foreground group-hover:text-primary transition-colors" />
            </div>
            <span className="text-sm text-muted-foreground group-hover:text-primary transition-colors">
              {link.label}
            </span>
          </a>
        ))}
      </motion.div>

      {/* Experience Timeline */}
      <motion.section variants={itemVariants} className="mb-16">
        <h2 className="text-2xl font-bold text-foreground mb-8 text-center flex items-center justify-center gap-3">
          <span className="w-8 h-1 rounded-full bg-primary" />
          经历与教育
          <span className="w-8 h-1 rounded-full bg-primary" />
        </h2>

        <div className="relative space-y-12">
          {/* Timeline Line */}
          <div className="absolute left-[20px] md:left-1/2 top-4 bottom-4 w-0.5 bg-gradient-to-b from-primary/50 to-transparent md:-translate-x-1/2" />

          {experiences.map((exp, index) => (
            <motion.div
              key={exp.id}
              className={`relative flex flex-col md:flex-row gap-8 ${index % 2 === 0 ? "md:text-right" : "md:flex-row-reverse"
                }`}
              initial={{ opacity: 0, y: 50 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: index * 0.1 }}
            >
              {/* Timeline Dot */}
              <div className="absolute left-[20px] md:left-1/2 w-4 h-4 rounded-full bg-black border-2 border-primary translate-y-6 md:-translate-x-2 z-10 shadow-[0_0_10px_rgba(59,130,246,0.5)]" />

              {/* Date (Mobile: hidden, Desktop: shown) */}
              <div className="hidden md:block flex-1 pt-4">
                <span className="text-primary font-mono font-medium">{exp.period}</span>
              </div>

              {/* Content */}
              <div className="flex-1 pl-12 md:pl-0">
                {/* Date (Mobile: shown) */}
                <div className="md:hidden text-primary font-mono font-medium mb-2 text-sm">{exp.period}</div>

                <div className={`p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-primary/30 transition-all group ${index % 2 === 0 ? "md:mr-8" : "md:ml-8"
                  }`}>
                  <h3 className="text-xl font-bold text-foreground mb-1 group-hover:text-primary transition-colors">
                    {exp.title}
                  </h3>
                  <div className="flex items-center gap-2 text-sm text-muted-foreground mb-4 md:justify-end">
                    <span className={index % 2 === 0 ? "md:order-2" : ""}>{exp.company}</span>
                    <span className="hidden md:inline">•</span>
                    <span className={index % 2 === 0 ? "md:order-1" : ""}>{exp.location}</span>
                  </div>
                  <p className="text-muted-foreground leading-relaxed">
                    {exp.description}
                  </p>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </motion.section>
    </motion.div>
  )
}
