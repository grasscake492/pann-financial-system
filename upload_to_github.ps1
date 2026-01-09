# PANN财务系统 - 上传到GitHub脚本
# 适用于Windows PowerShell

Write-Host "=========================================="
Write-Host "PANN财务系统 - 上传到GitHub"
Write-Host "开始时间: $(Get-Date)"
Write-Host "=========================================="

# 检查是否在Git仓库中
if (-not (Test-Path ".git")) {
    Write-Host "初始化Git仓库..."
    git init
    Write-Host "✅ Git仓库初始化成功"
}

# 添加所有文件到Git
Write-Host "添加文件到Git..."
git add .

# 检查是否有更改需要提交
$gitStatus = git status --porcelain
if ($gitStatus) {
    # 有更改，提交并推送
    $commitMessage = "更新PANN财务系统数据库 - $(Get-Date -Format 'yyyy-MM-dd')"
    
    Write-Host "提交更改: $commitMessage"
    git commit -m $commitMessage
    
    Write-Host "推送到GitHub..."
    
    # 检查当前分支
    $currentBranch = git branch --show-current
    if (-not $currentBranch) {
        $currentBranch = "main"
    }
    
    # 推送到远程仓库
    git push -u origin $currentBranch
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ 推送成功！"
    } else {
        Write-Host "❌ 推送失败，请检查远程仓库配置"
        Write-Host "可以运行以下命令添加远程仓库："
        Write-Host "  git remote add origin https://github.com/您的用户名/您的仓库名.git"
    }
} else {
    Write-Host "⚠️  没有更改需要提交"
}

Write-Host ""
Write-Host "=========================================="
Write-Host "上传完成！"
Write-Host "完成时间: $(Get-Date)"
Write-Host "=========================================="
