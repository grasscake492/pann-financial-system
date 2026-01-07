# one-click-deploy.ps1 - 一键构建部署
param(
    [string]$TomcatPath = "D:\tomcat\apache-tomcat-10.1.49-windows-x64\apache-tomcat-10.1.49",
    [string]$AppName = "pann-web",
    [switch]$SkipBuild = $false
)

Write-Host "=== 一键构建部署 ===" -ForegroundColor Cyan

# 1. 构建
if (-not $SkipBuild) {
    Write-Host "1. 构建项目..." -ForegroundColor Yellow
    
    if (Test-Path "reliable-build.ps1") {
        .\reliable-build.ps1
    } else {
        npm run build:prod
    }
    
    if (-not (Test-Path "dist")) {
        Write-Host "✗ 构建失败" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "1. 跳过构建" -ForegroundColor Gray
}

# 2. 检查 Tomcat
if (-not (Test-Path $TomcatPath)) {
    Write-Host "✗ Tomcat 路径不存在: $TomcatPath" -ForegroundColor Red
    Write-Host "构建文件在: dist 目录" -ForegroundColor Gray
    exit 1
}

# 3. 停止 Tomcat
Write-Host "`n2. 停止 Tomcat..." -ForegroundColor Yellow
& "$TomcatPath\bin\shutdown.bat" 2>&1 | Out-Null
Start-Sleep -Seconds 3

# 4. 部署
Write-Host "3. 部署文件..." -ForegroundColor Yellow
$deployDir = "$TomcatPath\webapps\$AppName"

if (Test-Path $deployDir) {
    # 备份
    $backupDir = "$TomcatPath\webapps\backup_$AppName_$(Get-Date -Format 'yyyyMMdd_HHmmss')"
    Write-Host "备份到: $backupDir" -ForegroundColor Gray
    Copy-Item $deployDir $backupDir -Recurse -Force
}

# 清理并部署
Remove-Item $deployDir -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item "dist\*" $deployDir -Recurse -Force
Write-Host "✓ 部署完成" -ForegroundColor Green

# 5. 启动 Tomcat
Write-Host "4. 启动 Tomcat..." -ForegroundColor Green
& "$TomcatPath\bin\startup.bat"

Write-Host "等待应用启动..." -ForegroundColor Gray
Start-Sleep -Seconds 8

# 6. 验证
$url = "http://localhost:8081/$AppName/"
Write-Host "`n5. 验证部署..." -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri $url -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✓ 应用可访问 (状态码: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "⚠ 应用可能还在启动中: $_" -ForegroundColor Yellow
}

# 7. 显示结果
Write-Host "`n=== 部署完成 ===" -ForegroundColor Cyan
Write-Host "前端地址: $url" -ForegroundColor Green
Write-Host "后端 API: http://localhost:8081/pann-financial-system/" -ForegroundColor Gray

# 打开浏览器
$open = Read-Host "`n是否打开浏览器？ (y/n) [默认: y]"
if ($open -ne 'n') {
    Start-Process $url
}

Write-Host "`n按任意键退出..." -ForegroundColor Gray
pause
