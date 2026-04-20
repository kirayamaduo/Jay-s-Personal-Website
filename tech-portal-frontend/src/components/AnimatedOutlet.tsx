import { useLocation, useOutlet } from "react-router-dom"
import { AnimatePresence, motion } from "framer-motion"



export function AnimatedOutlet() {
  const location = useLocation()
  const element = useOutlet()

  return (
    <AnimatePresence mode="wait">
      <motion.div
        key={location.pathname}
        initial={{ opacity: 0, y: 20, filter: 'blur(10px)' }} // 初始状态：模糊且下沉
        animate={{ opacity: 1, y: 0, filter: 'blur(0px)' }}   // 进场：清晰且上浮
        exit={{ opacity: 0, y: -20, filter: 'blur(5px)' }}    // 离场：模糊且上浮消失
        transition={{ duration: 0.3, ease: "easeOut" }}
        className="w-full h-full"
      >
        {element}
      </motion.div>
    </AnimatePresence>
  )
}
