param(
    [string]$filePath = "database/pann_financial_system_init.sql"
)

Write-Host "修复文件编码: $filePath"

# 1. 读取原始文件字节
$bytes = [System.IO.File]::ReadAllBytes($filePath)

# 2. 尝试用GBK编码解码（因为乱码显示GBK被误读为UTF-8）
$gbk = [System.Text.Encoding]::GetEncoding("GBK")
$content = $gbk.GetString($bytes)

Write-Host "检测到乱码内容示例:"
$sample = $content.Substring(0, [Math]::Min(500, $content.Length))
$sampleLines = $sample -split "`n"
$sampleLines | ForEach-Object {
    if ($_ -match "绯荤粺") {
        Write-Host "修复前: $_"
    }
}

# 3. 替换常见的乱码字符串
Write-Host "`n替换乱码字符串..."
$content = $content -replace "绯荤粺绠＄悊鍛?", "系统管理员"
$content = $content -replace "寮犱笁", "张三"
$content = $content -replace "鏂伴椈閮?", "新闻部"
$content = $content -replace "缂栬緫閮?", "编辑部"
$content = $content -replace "杩愯惀閮?", "运营部"

# 4. 保存为UTF-8 with BOM
[System.IO.File]::WriteAllText($filePath, $content, [System.Text.Encoding]::UTF8)

Write-Host "`n修复完成！验证结果:"

# 5. 验证修复
$fixedContent = Get-Content $filePath -Encoding UTF8 -TotalCount 50
$fixedContent | ForEach-Object {
    if ($_ -match "系统管理员" -or $_ -match "张三" -or $_ -match "新闻部") {
        Write-Host "✅ $_"
    }
}
