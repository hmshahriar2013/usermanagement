# Workspace Scanner Script
# PowerShell script to scan workspace and generate structure data

param(
    [Parameter(Mandatory=$true)]
    [string]$WorkspaceRoot,
    
    [Parameter(Mandatory=$false)]
    [int]$MaxDepth = 10,
    
    [Parameter(Mandatory=$false)]
    [string[]]$ExcludeDirs = @(
        ".git", "node_modules", "build", "dist", "target", 
        ".gradle", "venv", ".venv", "bin", "obj", 
        "__pycache__", "coverage", ".idea", ".vscode",
        "temp", "tmp", "cache"
    )
)

function Get-WorkspaceStructure {
    param(
        [string]$Path,
        [int]$CurrentDepth = 0,
        [int]$MaxDepth
    )
    
    if ($CurrentDepth -gt $MaxDepth) {
        return
    }
    
    $items = Get-ChildItem -Path $Path -Force -ErrorAction SilentlyContinue
    
    foreach ($item in $items) {
        # Skip excluded directories
        if ($item.PSIsContainer -and $ExcludeDirs -contains $item.Name) {
            continue
        }
        
        $relativePath = $item.FullName.Replace($WorkspaceRoot, "").TrimStart("\")
        
        if ($item.PSIsContainer) {
            Write-Output "DIR|$relativePath|$($item.Name)"
            Get-WorkspaceStructure -Path $item.FullName -CurrentDepth ($CurrentDepth + 1) -MaxDepth $MaxDepth
        } else {
            $sizeKB = [math]::Round($item.Length / 1KB, 2)
            Write-Output "FILE|$relativePath|$($item.Name)|$sizeKB"
        }
    }
}

# Main execution
Write-Output "SCAN_START|$WorkspaceRoot"
Get-WorkspaceStructure -Path $WorkspaceRoot -MaxDepth $MaxDepth
Write-Output "SCAN_END"
