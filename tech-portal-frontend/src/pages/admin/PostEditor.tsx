import { useState, useEffect } from "react"
import { useNavigate, useParams } from "react-router-dom"
import {
  ArrowLeft,
  Save,
  Loader2,
  Eye,
  Image as ImageIcon,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import {
  adminApi,
  tagsApi,
  type TagDTO,
  type CreatePostRequest,
} from "@/services/api"
import { toast } from "sonner"

export default function PostEditor() {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const isEditing = !!id

  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [allTags, setAllTags] = useState<TagDTO[]>([])

  // Form state
  const [title, setTitle] = useState("")
  const [slug, setSlug] = useState("")
  const [excerpt, setExcerpt] = useState("")
  const [content, setContent] = useState("")
  const [coverImage, setCoverImage] = useState("")
  const [seoTitle, setSeoTitle] = useState("")
  const [seoDescription, setSeoDescription] = useState("")
  const [isFeatured, setIsFeatured] = useState(false)
  const [isPublished, setIsPublished] = useState(false)
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([])

  // Load tags
  useEffect(() => {
    const loadTags = async () => {
      try {
        const tags = await tagsApi.getAll()
        setAllTags(tags)
      } catch (error) {
        console.error("Failed to load tags:", error)
      }
    }
    loadTags()
  }, [])

  // Load post data if editing
  useEffect(() => {
    if (isEditing) {
      const loadPost = async () => {
        setLoading(true)
        try {
          const post = await adminApi.posts.getById(Number(id))
          setTitle(post.title)
          setSlug(post.slug)
          setExcerpt(post.excerpt || "")
          setContent(post.content || "")
          setCoverImage(post.coverImage || "")
          setSeoTitle(post.seoTitle || "")
          setSeoDescription(post.seoDescription || "")
          setIsFeatured(post.isFeatured)
          setIsPublished(true) // If we can fetch it, it exists
          setSelectedTagIds(post.tags.map((t) => t.id))
        } catch (error) {
          console.error("Failed to load post:", error)
          toast.error("加载文章失败")
          navigate("/admin/posts")
        } finally {
          setLoading(false)
        }
      }
      loadPost()
    }
  }, [id, isEditing, navigate])

  // Auto generate slug from title
  const generateSlug = (text: string) => {
    return text
      .toLowerCase()
      .replace(/[^a-z0-9\u4e00-\u9fa5\s-]/g, "")
      .replace(/\s+/g, "-")
      .replace(/-+/g, "-")
      .replace(/^-|-$/g, "")
  }

  const handleTitleChange = (value: string) => {
    setTitle(value)
    if (!isEditing && !slug) {
      setSlug(generateSlug(value))
    }
  }

  const toggleTag = (tagId: number) => {
    setSelectedTagIds((prev) =>
      prev.includes(tagId)
        ? prev.filter((id) => id !== tagId)
        : [...prev, tagId]
    )
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!title.trim()) {
      toast.error("请输入文章标题")
      return
    }
    if (!content.trim()) {
      toast.error("请输入文章内容")
      return
    }

    setSaving(true)
    try {
      const data: CreatePostRequest = {
        title: title.trim(),
        slug: slug.trim() || undefined,
        excerpt: excerpt.trim() || undefined,
        content: content.trim(),
        coverImage: coverImage.trim() || undefined,
        seoTitle: seoTitle.trim() || undefined,
        seoDescription: seoDescription.trim() || undefined,
        isFeatured,
        isPublished,
        tagIds: selectedTagIds.length > 0 ? selectedTagIds : undefined,
      }

      if (isEditing) {
        await adminApi.posts.update(Number(id), data)
        toast.success("文章已更新")
      } else {
        await adminApi.posts.create(data)
        toast.success("文章已创建")
      }

      navigate("/admin/posts")
    } catch (error) {
      console.error("Failed to save post:", error)
      toast.error(isEditing ? "更新失败" : "创建失败")
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate("/admin/posts")}
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            返回
          </Button>
          <h1 className="text-2xl font-bold text-foreground">
            {isEditing ? "编辑文章" : "写新文章"}
          </h1>
        </div>
        <div className="flex items-center gap-2">
          {slug && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => window.open(`/blog/${slug}`, "_blank")}
              className="border-white/10"
            >
              <Eye className="w-4 h-4 mr-2" />
              预览
            </Button>
          )}
          <Button
            onClick={handleSubmit}
            disabled={saving}
            className="bg-primary hover:bg-primary/90"
          >
            {saving ? (
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
            ) : (
              <Save className="w-4 h-4 mr-2" />
            )}
            {isEditing ? "保存修改" : "发布文章"}
          </Button>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Title */}
        <div className="space-y-2">
          <Label htmlFor="title">文章标题 *</Label>
          <Input
            id="title"
            value={title}
            onChange={(e) => handleTitleChange(e.target.value)}
            placeholder="输入文章标题..."
            className="bg-white/5 border-white/10 text-lg font-medium"
          />
        </div>

        {/* Slug */}
        <div className="space-y-2">
          <Label htmlFor="slug">URL 标识 (Slug)</Label>
          <Input
            id="slug"
            value={slug}
            onChange={(e) => setSlug(e.target.value)}
            placeholder="auto-generated-from-title"
            className="bg-white/5 border-white/10 font-mono text-sm"
          />
          <p className="text-xs text-muted-foreground">
            留空将根据标题自动生成，用于文章的 URL 地址
          </p>
        </div>

        {/* Excerpt */}
        <div className="space-y-2">
          <Label htmlFor="excerpt">文章摘要</Label>
          <Textarea
            id="excerpt"
            value={excerpt}
            onChange={(e) => setExcerpt(e.target.value)}
            placeholder="简短描述文章内容..."
            rows={2}
            className="bg-white/5 border-white/10 resize-none"
          />
        </div>

        {/* Content */}
        <div className="space-y-2">
          <Label htmlFor="content">文章内容 * (支持 Markdown)</Label>
          <Textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="开始写作..."
            rows={15}
            className="bg-white/5 border-white/10 font-mono text-sm resize-y min-h-[300px]"
          />
        </div>

        {/* Cover Image */}
        <div className="space-y-2">
          <Label htmlFor="coverImage">封面图片 URL</Label>
          <div className="flex gap-2">
            <Input
              id="coverImage"
              value={coverImage}
              onChange={(e) => setCoverImage(e.target.value)}
              placeholder="https://example.com/image.jpg"
              className="bg-white/5 border-white/10"
            />
            {coverImage && (
              <Button
                type="button"
                variant="outline"
                size="icon"
                onClick={() => window.open(coverImage, "_blank")}
                className="border-white/10 shrink-0"
              >
                <ImageIcon className="w-4 h-4" />
              </Button>
            )}
          </div>
        </div>

        {/* Tags */}
        <div className="space-y-2">
          <Label>标签</Label>
          <div className="flex flex-wrap gap-2 p-3 rounded-lg bg-white/5 border border-white/10">
            {allTags.length === 0 ? (
              <p className="text-sm text-muted-foreground">暂无可用标签</p>
            ) : (
              allTags.map((tag) => (
                <Badge
                  key={tag.id}
                  variant={selectedTagIds.includes(tag.id) ? "default" : "secondary"}
                  className={`cursor-pointer transition-colors ${
                    selectedTagIds.includes(tag.id)
                      ? "bg-primary text-primary-foreground"
                      : "hover:bg-white/10"
                  }`}
                  onClick={() => toggleTag(tag.id)}
                >
                  {tag.name}
                </Badge>
              ))
            )}
          </div>
        </div>

        {/* SEO Section */}
        <div className="p-4 rounded-lg bg-white/5 border border-white/10 space-y-4">
          <h3 className="font-medium text-foreground">SEO 设置</h3>
          
          <div className="space-y-2">
            <Label htmlFor="seoTitle">SEO 标题</Label>
            <Input
              id="seoTitle"
              value={seoTitle}
              onChange={(e) => setSeoTitle(e.target.value)}
              placeholder="留空将使用文章标题"
              className="bg-white/5 border-white/10"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="seoDescription">SEO 描述</Label>
            <Textarea
              id="seoDescription"
              value={seoDescription}
              onChange={(e) => setSeoDescription(e.target.value)}
              placeholder="留空将使用文章摘要"
              rows={2}
              className="bg-white/5 border-white/10 resize-none"
            />
          </div>
        </div>

        {/* Options */}
        <div className="flex flex-wrap gap-6 p-4 rounded-lg bg-white/5 border border-white/10">
          <div className="flex items-center gap-3">
            <Switch
              id="isPublished"
              checked={isPublished}
              onCheckedChange={setIsPublished}
            />
            <Label htmlFor="isPublished" className="cursor-pointer">
              立即发布
            </Label>
          </div>

          <div className="flex items-center gap-3">
            <Switch
              id="isFeatured"
              checked={isFeatured}
              onCheckedChange={setIsFeatured}
            />
            <Label htmlFor="isFeatured" className="cursor-pointer">
              设为精选
            </Label>
          </div>
        </div>
      </form>
    </div>
  )
}
