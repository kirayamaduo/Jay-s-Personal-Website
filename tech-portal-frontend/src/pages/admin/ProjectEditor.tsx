import { useState, useEffect } from "react"
import { useNavigate, useParams } from "react-router-dom"
import {
  ArrowLeft,
  Save,
  Loader2,
  Eye,
  Plus,
  X,
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
  type CreateProjectRequest,
} from "@/services/api"
import { toast } from "sonner"

export default function ProjectEditor() {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const isEditing = !!id

  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [allTags, setAllTags] = useState<TagDTO[]>([])

  // Form state
  const [title, setTitle] = useState("")
  const [slug, setSlug] = useState("")
  const [description, setDescription] = useState("")
  const [content, setContent] = useState("")
  const [icon, setIcon] = useState("")
  const [gradientClass, setGradientClass] = useState("")
  const [githubUrl, setGithubUrl] = useState("")
  const [demoUrl, setDemoUrl] = useState("")
  const [isFeatured, setIsFeatured] = useState(false)
  const [isActive, setIsActive] = useState(true)
  const [displayOrder, setDisplayOrder] = useState(0)
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([])

  // Tech Stack
  const [techStack, setTechStack] = useState<{ name: string; description: string }[]>([])
  const [newTechName, setNewTechName] = useState("")
  const [newTechDesc, setNewTechDesc] = useState("")

  // Features
  const [features, setFeatures] = useState<string[]>([])
  const [newFeature, setNewFeature] = useState("")

  // Gallery Images
  const [galleryImages, setGalleryImages] = useState<string[]>([])
  const [newImageUrl, setNewImageUrl] = useState("")

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

  // Load project data if editing
  useEffect(() => {
    if (isEditing) {
      const loadProject = async () => {
        setLoading(true)
        try {
          const project = await adminApi.projects.getById(Number(id))
          setTitle(project.title)
          setSlug(project.slug)
          setDescription(project.description)
          setContent(project.content || "")
          setIcon(project.icon || "")
          setGradientClass(project.gradientClass || "")
          setGithubUrl(project.githubUrl || "")
          setDemoUrl(project.demoUrl || "")
          setIsFeatured(project.isFeatured)
          setIsActive(true)
          setDisplayOrder(project.displayOrder)
          setSelectedTagIds(project.tags.map((t) => t.id))
          setTechStack(project.techStack || [])
          setFeatures(project.features || [])
          setGalleryImages(project.galleryImages || [])
        } catch (error) {
          console.error("Failed to load project:", error)
          toast.error("加载项目失败")
          navigate("/admin/projects")
        } finally {
          setLoading(false)
        }
      }
      loadProject()
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

  // Tech Stack handlers
  const addTech = () => {
    if (!newTechName.trim()) return
    setTechStack([...techStack, { name: newTechName.trim(), description: newTechDesc.trim() }])
    setNewTechName("")
    setNewTechDesc("")
  }

  const removeTech = (index: number) => {
    setTechStack(techStack.filter((_, i) => i !== index))
  }

  // Features handlers
  const addFeature = () => {
    if (!newFeature.trim()) return
    setFeatures([...features, newFeature.trim()])
    setNewFeature("")
  }

  const removeFeature = (index: number) => {
    setFeatures(features.filter((_, i) => i !== index))
  }

  // Gallery handlers
  const addImage = () => {
    if (!newImageUrl.trim()) return
    setGalleryImages([...galleryImages, newImageUrl.trim()])
    setNewImageUrl("")
  }

  const removeImage = (index: number) => {
    setGalleryImages(galleryImages.filter((_, i) => i !== index))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!title.trim()) {
      toast.error("请输入项目名称")
      return
    }
    if (!description.trim()) {
      toast.error("请输入项目描述")
      return
    }

    setSaving(true)
    try {
      const data: CreateProjectRequest = {
        title: title.trim(),
        slug: slug.trim() || undefined,
        description: description.trim(),
        content: content.trim() || undefined,
        icon: icon.trim() || undefined,
        gradientClass: gradientClass.trim() || undefined,
        githubUrl: githubUrl.trim() || undefined,
        demoUrl: demoUrl.trim() || undefined,
        isFeatured,
        isActive,
        displayOrder,
        tagIds: selectedTagIds.length > 0 ? selectedTagIds : undefined,
        techStack: techStack.length > 0 ? techStack : undefined,
        features: features.length > 0 ? features : undefined,
        galleryImages: galleryImages.length > 0 ? galleryImages : undefined,
      }

      if (isEditing) {
        await adminApi.projects.update(Number(id), data)
        toast.success("项目已更新")
      } else {
        await adminApi.projects.create(data)
        toast.success("项目已创建")
      }

      navigate("/admin/projects")
    } catch (error) {
      console.error("Failed to save project:", error)
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
            onClick={() => navigate("/admin/projects")}
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            返回
          </Button>
          <h1 className="text-2xl font-bold text-foreground">
            {isEditing ? "编辑项目" : "添加项目"}
          </h1>
        </div>
        <div className="flex items-center gap-2">
          {slug && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => window.open(`/projects/${slug}`, "_blank")}
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
            {isEditing ? "保存修改" : "创建项目"}
          </Button>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Basic Info */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label htmlFor="title">项目名称 *</Label>
            <Input
              id="title"
              value={title}
              onChange={(e) => handleTitleChange(e.target.value)}
              placeholder="My Awesome Project"
              className="bg-white/5 border-white/10"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="slug">URL 标识 (Slug)</Label>
            <Input
              id="slug"
              value={slug}
              onChange={(e) => setSlug(e.target.value)}
              placeholder="my-awesome-project"
              className="bg-white/5 border-white/10 font-mono text-sm"
            />
          </div>
        </div>

        {/* Icon and Gradient */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label htmlFor="icon">项目图标 (Emoji)</Label>
            <Input
              id="icon"
              value={icon}
              onChange={(e) => setIcon(e.target.value)}
              placeholder="🚀"
              className="bg-white/5 border-white/10"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="gradientClass">渐变样式</Label>
            <Input
              id="gradientClass"
              value={gradientClass}
              onChange={(e) => setGradientClass(e.target.value)}
              placeholder="from-blue-500/20 via-cyan-500/20 to-teal-500/20"
              className="bg-white/5 border-white/10 font-mono text-sm"
            />
          </div>
        </div>

        {/* Description */}
        <div className="space-y-2">
          <Label htmlFor="description">项目描述 *</Label>
          <Textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="简短介绍这个项目..."
            rows={2}
            className="bg-white/5 border-white/10 resize-none"
          />
        </div>

        {/* Content */}
        <div className="space-y-2">
          <Label htmlFor="content">详细介绍 (支持 Markdown)</Label>
          <Textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="项目的详细说明..."
            rows={8}
            className="bg-white/5 border-white/10 font-mono text-sm resize-y"
          />
        </div>

        {/* Links */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label htmlFor="githubUrl">GitHub 链接</Label>
            <Input
              id="githubUrl"
              value={githubUrl}
              onChange={(e) => setGithubUrl(e.target.value)}
              placeholder="https://github.com/username/repo"
              className="bg-white/5 border-white/10"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="demoUrl">演示链接</Label>
            <Input
              id="demoUrl"
              value={demoUrl}
              onChange={(e) => setDemoUrl(e.target.value)}
              placeholder="https://demo.example.com"
              className="bg-white/5 border-white/10"
            />
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

        {/* Tech Stack */}
        <div className="p-4 rounded-lg bg-white/5 border border-white/10 space-y-4">
          <h3 className="font-medium text-foreground">技术栈</h3>
          
          <div className="flex flex-wrap gap-2">
            {techStack.map((tech, index) => (
              <div
                key={index}
                className="flex items-center gap-1 px-2 py-1 rounded bg-white/10"
              >
                <span className="text-sm font-medium">{tech.name}</span>
                {tech.description && (
                  <span className="text-xs text-muted-foreground">
                    ({tech.description})
                  </span>
                )}
                <button
                  type="button"
                  onClick={() => removeTech(index)}
                  className="ml-1 text-muted-foreground hover:text-red-400"
                >
                  <X className="w-3 h-3" />
                </button>
              </div>
            ))}
          </div>

          <div className="flex gap-2">
            <Input
              value={newTechName}
              onChange={(e) => setNewTechName(e.target.value)}
              placeholder="技术名称"
              className="bg-white/5 border-white/10 flex-1"
              onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), addTech())}
            />
            <Input
              value={newTechDesc}
              onChange={(e) => setNewTechDesc(e.target.value)}
              placeholder="用途说明 (可选)"
              className="bg-white/5 border-white/10 flex-1"
              onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), addTech())}
            />
            <Button
              type="button"
              variant="outline"
              size="icon"
              onClick={addTech}
              className="border-white/10 shrink-0"
            >
              <Plus className="w-4 h-4" />
            </Button>
          </div>
        </div>

        {/* Features */}
        <div className="p-4 rounded-lg bg-white/5 border border-white/10 space-y-4">
          <h3 className="font-medium text-foreground">功能特性</h3>
          
          <div className="space-y-2">
            {features.map((feature, index) => (
              <div
                key={index}
                className="flex items-center gap-2 px-3 py-2 rounded bg-white/10"
              >
                <span className="flex-1 text-sm">{feature}</span>
                <button
                  type="button"
                  onClick={() => removeFeature(index)}
                  className="text-muted-foreground hover:text-red-400"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>

          <div className="flex gap-2">
            <Input
              value={newFeature}
              onChange={(e) => setNewFeature(e.target.value)}
              placeholder="添加功能特性..."
              className="bg-white/5 border-white/10"
              onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), addFeature())}
            />
            <Button
              type="button"
              variant="outline"
              size="icon"
              onClick={addFeature}
              className="border-white/10 shrink-0"
            >
              <Plus className="w-4 h-4" />
            </Button>
          </div>
        </div>

        {/* Gallery Images */}
        <div className="p-4 rounded-lg bg-white/5 border border-white/10 space-y-4">
          <h3 className="font-medium text-foreground">项目截图</h3>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
            {galleryImages.map((url, index) => (
              <div key={index} className="relative group aspect-video">
                <img
                  src={url}
                  alt={`Screenshot ${index + 1}`}
                  className="w-full h-full object-cover rounded border border-white/10"
                />
                <button
                  type="button"
                  onClick={() => removeImage(index)}
                  className="absolute top-1 right-1 p-1 rounded bg-black/50 text-white opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <X className="w-3 h-3" />
                </button>
              </div>
            ))}
          </div>

          <div className="flex gap-2">
            <Input
              value={newImageUrl}
              onChange={(e) => setNewImageUrl(e.target.value)}
              placeholder="图片 URL..."
              className="bg-white/5 border-white/10"
              onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), addImage())}
            />
            <Button
              type="button"
              variant="outline"
              size="icon"
              onClick={addImage}
              className="border-white/10 shrink-0"
            >
              <ImageIcon className="w-4 h-4" />
            </Button>
          </div>
        </div>

        {/* Options */}
        <div className="flex flex-wrap gap-6 p-4 rounded-lg bg-white/5 border border-white/10">
          <div className="flex items-center gap-3">
            <Switch
              id="isActive"
              checked={isActive}
              onCheckedChange={setIsActive}
            />
            <Label htmlFor="isActive" className="cursor-pointer">
              公开显示
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

          <div className="flex items-center gap-3">
            <Label htmlFor="displayOrder">排序</Label>
            <Input
              id="displayOrder"
              type="number"
              value={displayOrder}
              onChange={(e) => setDisplayOrder(Number(e.target.value))}
              className="bg-white/5 border-white/10 w-20"
            />
          </div>
        </div>
      </form>
    </div>
  )
}
