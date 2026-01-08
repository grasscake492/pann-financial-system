# reliable-build.ps1 - 可靠的构建脚本
param(
    [string]$Mode = "production",
    [string]$Minify = "auto"  # auto, esbuild, terser
)

Write-Host "=== 可靠构建脚本 ===" -ForegroundColor Cyan
Write-Host "模式: $Mode" -ForegroundColor Gray
Write-Host "压缩工具: $Minify" -ForegroundColor Gray

# 检查依赖
Write-Host "`n1. 检查环境..." -ForegroundColor Yellow

# 检查 node_modules
if (-not (Test-Path "node_modules")) {
    Write-Host "✗ node_modules 不存在，正在安装依赖..." -ForegroundColor Red
    npm install
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "✗ 依赖安装失败" -ForegroundColor Red
        exit 1
    }
}

# 自动选择压缩工具
if ($Minify -eq "auto") {
    Write-Host "测试 terser..." -ForegroundColor Gray
    npx terser --version 2>&1 | Out-Null
    
    if ($LASTEXITCODE -eq 0) {
        $Minify = "terser"
        Write-Host "  选择 terser" -ForegroundColor Green
    } else {
        $Minify = "esbuild"
        Write-Host "  选择 esbuild (terser 不可用)" -ForegroundColor Yellow
    }
}

# 构建命令
$buildCmd = "vite build"
if ($Mode -ne "production") {
    $buildCmd += " --mode $Mode"
}
$buildCmd += " --minify $Minify"

Write-Host "`n2. 执行构建: $buildCmd" -ForegroundColor Yellow

# 执行构建
Invoke-Expression $buildCmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ 构建成功！" -ForegroundColor Green
    
    # 显示构建结果
    if (Test-Path "dist") {
        $totalSize = (Get-ChildItem dist -Recurse -File | Measure-Object Length -Sum).Sum
        $totalMB = [math]::Round($totalSize / 1MB, 2)
        
        Write-Host "构建统计:" -ForegroundColor Gray
        Write-Host "  总大小: $totalMB MB" -ForegroundColor Gray
        Write-Host "  文件数: $(Get-ChildItem dist -Recurse -File | Measure-Object).Count" -ForegroundColor Gray
        
        # 显示主要文件
        Write-Host "主要文件:" -ForegroundColor Gray
        Get-ChildItem dist -File | Sort-Object Length -Descending | Select-Object -First 5 | ForEach-Object {
            $size = [math]::Round($_.Length / 1KB, 2)
            Write-Host "  $($_.Name) ($size KB)" -ForegroundColor DarkGray
        }
    }
} else {
    Write-Host "`n✗ 构建失败" -ForegroundColor Red
    
    # 尝试备选方案
    if ($Minify -eq "terser") {
        Write-Host "尝试使用 esbuild..." -ForegroundColor Yellow
        Invoke-Expression ($buildCmd -replace "terser", "esbuild")
    }
}

Write-Host "`n=== 构建完成 ===" -ForegroundColor Cyan
