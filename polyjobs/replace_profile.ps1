$path = "d:\TT\canhan\polyjobs\src\main\resources\templates\profile.html"
$content = Get-Content -Path $path -Raw
$content = $content.Replace('${!user.role}', '${user.role == false}')
$content = $content.Replace('${user.role}', '${user.role == true}')
Set-Content -Path $path -Value $content -Encoding UTF8
