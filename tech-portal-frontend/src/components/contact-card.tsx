import { useState } from "react"
import { Mail, Send, Loader2, CheckCircle } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { messagesApi, type CreateMessageRequest } from "@/services/api"
import { toast } from "sonner"

interface FormData {
  name: string
  email: string
  subject: string
  content: string
}

interface FormErrors {
  name?: string
  email?: string
  content?: string
}

export function ContactCard() {
  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [formData, setFormData] = useState<FormData>({
    name: "",
    email: "",
    subject: "",
    content: "",
  })
  const [errors, setErrors] = useState<FormErrors>({})

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {}

    if (!formData.name.trim()) {
      newErrors.name = "请输入您的姓名"
    }

    if (!formData.email.trim()) {
      newErrors.email = "请输入您的邮箱"
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "请输入有效的邮箱地址"
    }

    if (!formData.content.trim()) {
      newErrors.content = "请输入消息内容"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) return

    setLoading(true)
    try {
      const request: CreateMessageRequest = {
        name: formData.name.trim(),
        email: formData.email.trim(),
        subject: formData.subject.trim() || undefined,
        content: formData.content.trim(),
      }

      await messagesApi.send(request)
      setSuccess(true)
      toast.success("消息发送成功！", {
        description: "感谢您的留言，我会尽快回复您。",
      })

      // 重置表单
      setTimeout(() => {
        setOpen(false)
        setSuccess(false)
        setFormData({ name: "", email: "", subject: "", content: "" })
      }, 2000)
    } catch (error) {
      console.error("Failed to send message:", error)
      toast.error("发送失败", {
        description: "请稍后重试或直接发送邮件联系我。",
      })
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
    // 清除对应字段的错误
    if (errors[name as keyof FormErrors]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }))
    }
  }

  return (
    <div className="glass rounded-2xl p-6 h-full flex flex-col justify-between group hover:border-primary/30 transition-colors duration-300">
      <div>
        <h3 className="text-lg font-semibold text-foreground mb-3 flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-primary" />
          Get in Touch
        </h3>
        <p className="text-muted-foreground text-sm mb-4">
          {"Let's discuss your project or just say hi!"}
        </p>
      </div>
      <div className="space-y-3">
        <a
          href="mailto:hello@alexchen.dev"
          className="flex items-center gap-2 text-sm text-muted-foreground hover:text-primary transition-colors"
        >
          <Mail className="w-4 h-4" />
          hello@alexchen.dev
        </a>

        <Dialog open={open} onOpenChange={setOpen}>
          <DialogTrigger asChild>
            <Button className="w-full bg-primary hover:bg-primary/90 text-primary-foreground group">
              <Send className="w-4 h-4 mr-2 group-hover:translate-x-1 transition-transform" />
              Send Message
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-md bg-black/90 border-white/10 backdrop-blur-xl">
            <DialogHeader>
              <DialogTitle className="text-foreground">发送消息</DialogTitle>
              <DialogDescription>
                填写下方表单，我会尽快回复您。
              </DialogDescription>
            </DialogHeader>

            {success ? (
              <div className="flex flex-col items-center justify-center py-8 gap-4">
                <CheckCircle className="w-16 h-16 text-green-500" />
                <p className="text-foreground font-medium">消息已发送！</p>
                <p className="text-muted-foreground text-sm">感谢您的留言</p>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="name">
                    姓名 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    id="name"
                    name="name"
                    placeholder="请输入您的姓名"
                    value={formData.name}
                    onChange={handleInputChange}
                    disabled={loading}
                    aria-invalid={!!errors.name}
                    className="bg-white/5 border-white/10"
                  />
                  {errors.name && (
                    <p className="text-red-500 text-xs">{errors.name}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="email">
                    邮箱 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    id="email"
                    name="email"
                    type="email"
                    placeholder="your@email.com"
                    value={formData.email}
                    onChange={handleInputChange}
                    disabled={loading}
                    aria-invalid={!!errors.email}
                    className="bg-white/5 border-white/10"
                  />
                  {errors.email && (
                    <p className="text-red-500 text-xs">{errors.email}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="subject">主题</Label>
                  <Input
                    id="subject"
                    name="subject"
                    placeholder="消息主题（选填）"
                    value={formData.subject}
                    onChange={handleInputChange}
                    disabled={loading}
                    className="bg-white/5 border-white/10"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="content">
                    消息内容 <span className="text-red-500">*</span>
                  </Label>
                  <Textarea
                    id="content"
                    name="content"
                    placeholder="请输入您想说的话..."
                    rows={4}
                    value={formData.content}
                    onChange={handleInputChange}
                    disabled={loading}
                    aria-invalid={!!errors.content}
                    className="bg-white/5 border-white/10 resize-none"
                  />
                  {errors.content && (
                    <p className="text-red-500 text-xs">{errors.content}</p>
                  )}
                </div>

                <Button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-primary hover:bg-primary/90"
                >
                  {loading ? (
                    <>
                      <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                      发送中...
                    </>
                  ) : (
                    <>
                      <Send className="w-4 h-4 mr-2" />
                      发送消息
                    </>
                  )}
                </Button>
              </form>
            )}
          </DialogContent>
        </Dialog>
      </div>
    </div>
  )
}
