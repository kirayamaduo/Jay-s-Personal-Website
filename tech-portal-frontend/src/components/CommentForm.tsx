import { useState } from "react"
import { Send, Loader2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import type { CreateCommentRequest } from "@/services/api"

interface CommentFormProps {
  onSubmit: (data: CreateCommentRequest) => Promise<boolean>
  submitting?: boolean
  replyTo?: {
    id: number
    nickname: string
  }
  onCancelReply?: () => void
}

interface FormErrors {
  nickname?: string
  content?: string
}

export function CommentForm({
  onSubmit,
  submitting = false,
  replyTo,
  onCancelReply,
}: CommentFormProps) {
  const [formData, setFormData] = useState({
    nickname: "",
    email: "",
    website: "",
    content: "",
  })
  const [errors, setErrors] = useState<FormErrors>({})

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {}

    if (!formData.nickname.trim()) {
      newErrors.nickname = "请输入昵称"
    }

    if (!formData.content.trim()) {
      newErrors.content = "请输入评论内容"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) return

    const request: CreateCommentRequest = {
      nickname: formData.nickname.trim(),
      content: formData.content.trim(),
      email: formData.email.trim() || undefined,
      website: formData.website.trim() || undefined,
      parentId: replyTo?.id,
    }

    const success = await onSubmit(request)

    if (success) {
      // 只清空内容，保留用户信息方便继续评论
      setFormData((prev) => ({ ...prev, content: "" }))
      setErrors({})
      onCancelReply?.()
    }
  }

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
    if (errors[name as keyof FormErrors]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }))
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {replyTo && (
        <div className="flex items-center justify-between p-3 rounded-lg bg-primary/10 border border-primary/20">
          <span className="text-sm text-muted-foreground">
            回复 <span className="text-primary font-medium">@{replyTo.nickname}</span>
          </span>
          <Button
            type="button"
            variant="ghost"
            size="sm"
            onClick={onCancelReply}
            className="h-auto py-1 px-2 text-xs"
          >
            取消回复
          </Button>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="space-y-2">
          <Label htmlFor="nickname">
            昵称 <span className="text-red-500">*</span>
          </Label>
          <Input
            id="nickname"
            name="nickname"
            placeholder="你的昵称"
            value={formData.nickname}
            onChange={handleInputChange}
            disabled={submitting}
            aria-invalid={!!errors.nickname}
            className="bg-white/5 border-white/10"
          />
          {errors.nickname && (
            <p className="text-red-500 text-xs">{errors.nickname}</p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="email">邮箱</Label>
          <Input
            id="email"
            name="email"
            type="email"
            placeholder="不会公开显示"
            value={formData.email}
            onChange={handleInputChange}
            disabled={submitting}
            className="bg-white/5 border-white/10"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="website">网站</Label>
          <Input
            id="website"
            name="website"
            placeholder="https://..."
            value={formData.website}
            onChange={handleInputChange}
            disabled={submitting}
            className="bg-white/5 border-white/10"
          />
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="content">
          评论 <span className="text-red-500">*</span>
        </Label>
        <Textarea
          id="content"
          name="content"
          placeholder={replyTo ? `回复 @${replyTo.nickname}...` : "写下你的想法..."}
          rows={4}
          value={formData.content}
          onChange={handleInputChange}
          disabled={submitting}
          aria-invalid={!!errors.content}
          className="bg-white/5 border-white/10 resize-none"
        />
        {errors.content && (
          <p className="text-red-500 text-xs">{errors.content}</p>
        )}
      </div>

      <div className="flex justify-end">
        <Button
          type="submit"
          disabled={submitting}
          className="bg-primary hover:bg-primary/90"
        >
          {submitting ? (
            <>
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
              提交中...
            </>
          ) : (
            <>
              <Send className="w-4 h-4 mr-2" />
              {replyTo ? "回复" : "发表评论"}
            </>
          )}
        </Button>
      </div>
    </form>
  )
}
