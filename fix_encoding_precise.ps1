# 精确修复脚本
param(
    [string]$filePath = "database/pann_financial_system_init.sql"
)

Write-Host "开始精确修复..."
Write-Host "文件: $filePath"

# 备份
$backupFile = "$filePath.backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')"
Copy-Item $filePath $backupFile
Write-Host "已备份到: $backupFile"

# 读取字节
$bytes = [System.IO.File]::ReadAllBytes($filePath)

# 尝试找到正确的编码
$correctEncoding = $null
$correctContent = $null

foreach ($encodingName in @("GBK", "GB2312", "GB18030", "UTF-8")) {
    try {
        $encoding = [System.Text.Encoding]::GetEncoding($encodingName)
        $content = $encoding.GetString($bytes)
        
        # 检查是否有乱码迹象
        $hasCorrectChinese = $content -match "系统管理员|张三|新闻部|编辑部|运营部"
        $hasGarbled = $content -match "绯荤粺|寮犱笁|鏂伴椈|缂栬緫|杩愯惀"
        
        if ($hasCorrectChinese) {
            Write-Host "✅ $($encoding.EncodingName): 包含正确中文"
            $correctEncoding = $encoding
            $correctContent = $content
            break
        } elseif ($hasGarbled) {
            Write-Host "⚠️  $($encoding.EncodingName): 包含乱码"
            # 如果我们看到乱码，这可能就是文件的实际编码
            # 但我们需要用不同的编码来读取
        }
    } catch {
        Write-Host "❌ $encodingName: 解码失败"
    }
}

# 如果没找到正确编码，尝试转码
if (-not $correctEncoding) {
    Write-Host "未找到正确编码，尝试转码修复..."
    
    # 假设文件是GBK编码但被错误地保存了
    # 首先用GBK读取
    $gbk = [System.Text.Encoding]::GetEncoding("GBK")
    $content = $gbk.GetString($bytes)
    
    Write-Host "用GBK解码后的内容前200字符:"
    Write-Host $content.Substring(0, [Math]::Min(200, $content.Length))
    
    # 手动修复常见的乱码
    # 我们看到的乱码是GBK编码被误读为UTF-8的结果
    # 所以我们需要反转这个过程
    
    # 方法：将内容从GBK转到UTF-8
    # 但更好的方法是：用UTF-8重新编码GBK字符串
    $correctContent = $content
    
    # 如果内容中已经有乱码，需要替换
    # 但首先检查实际是什么
    Write-Host "`n搜索乱码字符串..."
    
    # 查找并替换所有乱码
    $replacements = @()
    
    # 尝试查找"系统管理员"的乱码形式
    $pattern = "绯荤粺[绠\w][＄\$][悊\w]鍛?"
    $matches = [regex]::Matches($content, $pattern)
    foreach ($match in $matches) {
        Write-Host "找到乱码: '$($match.Value)' 长度: $($match.Length)"
        $replacements += @{Old=$match.Value; New="系统管理员"}
    }
    
    # 查找"张三"
    $pattern = "寮犱笁"
    $matches = [regex]::Matches($content, $pattern)
    foreach ($match in $matches) {
        Write-Host "找到乱码: '$($match.Value)' -> 张三"
        $replacements += @{Old=$match.Value; New="张三"}
    }
    
    # 应用替换
    foreach ($replacement in $replacements) {
        $correctContent = $correctContent -replace [regex]::Escape($replacement.Old), $replacement.New
    }
}

# 保存为UTF-8 with BOM
if ($correctContent) {
    [System.IO.File]::WriteAllText($filePath, $correctContent, [System.Text.Encoding]::UTF8)
    Write-Host "✅ 文件已保存为UTF-8"
    
    # 验证
    Write-Host "`n验证修复结果:"
    $verifyContent = Get-Content $filePath -Encoding UTF8
    $chineseFound = @()
    
    foreach ($pattern in @("系统管理员", "张三", "新闻部", "编辑部", "运营部")) {
        if ($verifyContent -match $pattern) {
            $chineseFound += $pattern
        }
    }
    
    if ($chineseFound.Count -gt 0) {
        Write-Host "✅ 找到中文: $($chineseFound -join ', ')"
        
        # 显示一些例子
        Write-Host "`n示例行:"
        $verifyContent | Select-String -Pattern "系统管理员|张三|新闻部" | Select-Object -First 5 | ForEach-Object {
            Write-Host "  $_"
        }
    } else {
        Write-Host "❌ 没有找到中文，修复可能失败"
        Write-Host "文件开头内容:"
        Get-Content $filePath -Encoding UTF8 -TotalCount 20 | ForEach-Object { Write-Host "  $_" }
    }
} else {
    Write-Host "❌ 无法修复文件"
}
