# ----------------------------------------------------------------
# Pruebas del CRUD REST de /usuarios - ScoreSound - Semana 8
#
# Prueba de punta a punta: crear, listar, obtener, actualizar y
# borrar. Al final confirma que el usuario borrado ya no existe.
# Deja la evidencia en el archivo evidencia-pruebas.md
#
# Uso (con la API ya corriendo en otra terminal):
#   powershell -ExecutionPolicy Bypass -File .\probar-crud.ps1
# ----------------------------------------------------------------

param(
    [string]$Base = "http://localhost:8080/usuarios"
)

$archivo = Join-Path $PSScriptRoot "evidencia-pruebas.md"
$fence   = '```'
$pasos   = @()

function Invocar {
    param([string]$Metodo, [string]$Url, [string]$Cuerpo)

    $opciones = @{
        Method          = $Metodo
        Uri             = $Url
        UseBasicParsing = $true
        ContentType     = "application/json; charset=utf-8"
    }
    if ($Cuerpo) {
        $opciones.Body = [System.Text.Encoding]::UTF8.GetBytes($Cuerpo)
    }

    try {
        $r = Invoke-WebRequest @opciones
        return @{ Codigo = [int]$r.StatusCode; Cuerpo = [string]$r.Content }
    }
    catch {
        $resp = $_.Exception.Response
        if ($null -eq $resp) {
            Write-Host ""
            Write-Host "  No hay respuesta en $Url" -ForegroundColor Red
            Write-Host "  Revisa que la API este corriendo (mvnw spring-boot:run)."
            Write-Host ""
            exit 1
        }
        $texto = ""
        if ($_.ErrorDetails) { $texto = [string]$_.ErrorDetails.Message }
        return @{ Codigo = [int]$resp.StatusCode; Cuerpo = $texto }
    }
}

function Registrar {
    param($Numero, $Operacion, $Metodo, $Url, $Enviado, $Esperado, $Respuesta, [bool]$Extra = $true)

    $paso = ($Respuesta.Codigo -eq $Esperado) -and $Extra
    $script:pasos += [pscustomobject]@{
        Numero    = $Numero
        Operacion = $Operacion
        Metodo    = $Metodo
        Url       = $Url
        Enviado   = $Enviado
        Esperado  = $Esperado
        Obtenido  = $Respuesta.Codigo
        Cuerpo    = $Respuesta.Cuerpo
        Paso      = $paso
    }
    $color = if ($paso) { "Green" } else { "Red" }
    $marca = if ($paso) { "PASO " } else { "FALLO" }
    Write-Host ("  [{0}] {1}. {2,-11} {3,-6} {4}  -> {5}" -f $marca, $Numero, $Operacion, $Metodo, $Url, $Respuesta.Codigo) -ForegroundColor $color
}

Write-Host ""
Write-Host "  Probando el CRUD de $Base" -ForegroundColor Cyan
Write-Host ""

# Correo distinto en cada corrida, para no chocar con la regla de correo unico
$correo = "ana.torres." + (Get-Date -Format "HHmmss") + "@correo.com"

# 1. CREAR
$json = '{"nombre":"Ana Torres","correo":"' + $correo + '"}'
$r = Invocar "POST" $Base $json
$id = $null
try { $id = ($r.Cuerpo | ConvertFrom-Json).id } catch { }
Registrar 1 "Crear" "POST" $Base $json 201 $r ($null -ne $id)
if ($null -eq $id) {
    Write-Host "  No se obtuvo el id del usuario creado; no se puede seguir." -ForegroundColor Red
    exit 1
}
$urlId = "$Base/$id"

# 2. LISTAR
$r = Invocar "GET" $Base
$aparece = $false
try {
    # PowerShell 5.1 y 7 entregan los arreglos JSON distinto; se aplana igual en ambos
    $lista = @(@(ConvertFrom-Json -InputObject $r.Cuerpo) | ForEach-Object { $_ })
    $aparece = @($lista | Where-Object { $_.id -eq $id }).Count -eq 1
} catch { }
Registrar 2 "Listar" "GET" $Base "" 200 $r $aparece

# 3. OBTENER
$r = Invocar "GET" $urlId
Registrar 3 "Obtener" "GET" $urlId "" 200 $r

# 4. ACTUALIZAR
$json = '{"nombre":"Ana Maria Torres","correo":"' + $correo + '"}'
$r = Invocar "PUT" $urlId $json
$cambio = $false
try { $cambio = (($r.Cuerpo | ConvertFrom-Json).nombre -eq "Ana Maria Torres") } catch { }
Registrar 4 "Actualizar" "PUT" $urlId $json 200 $r $cambio

# 5. BORRAR
$r = Invocar "DELETE" $urlId
Registrar 5 "Borrar" "DELETE" $urlId "" 204 $r

# 6. CONFIRMAR BORRADO
$r = Invocar "GET" $urlId
Registrar 6 "Confirmar" "GET" $urlId "" 404 $r

# ---------------- Reporte ----------------

$aprobadas = @($pasos | Where-Object { $_.Paso }).Count
$total     = $pasos.Count

$md = @()
$md += "# Evidencia de pruebas - CRUD REST de usuarios"
$md += ""
$md += "ScoreSound - Semana 8 - Desarrollo Fullstack - CORHUILA"
$md += ""
$md += "- Fecha de la prueba: " + (Get-Date -Format "yyyy-MM-dd HH:mm:ss")
$md += "- API probada: $Base"
$md += "- Resultado: **$aprobadas de $total pruebas aprobadas**"
$md += ""
$md += "## Resumen"
$md += ""
$md += "| # | Operacion | Metodo | URL | Esperado | Obtenido | Resultado |"
$md += "|---|---|---|---|---|---|---|"
foreach ($p in $pasos) {
    $res = if ($p.Paso) { "Paso" } else { "Fallo" }
    $md += "| $($p.Numero) | $($p.Operacion) | $($p.Metodo) | $($p.Url) | $($p.Esperado) | $($p.Obtenido) | $res |"
}
$md += ""
$md += "La prueba 6 vuelve a pedir el usuario despues de borrarlo: el 404 confirma que el borrado se hizo de verdad."
$md += ""
$md += "## Detalle de cada peticion"

foreach ($p in $pasos) {
    $md += ""
    $md += "### $($p.Numero). $($p.Operacion) - $($p.Metodo) $($p.Url)"
    $md += ""
    if ($p.Enviado) {
        $md += "Cuerpo enviado:"
        $md += ""
        $md += $fence + "json"
        $md += $p.Enviado
        $md += $fence
        $md += ""
    }
    $md += "Codigo esperado: **$($p.Esperado)** - Codigo obtenido: **$($p.Obtenido)**"
    $md += ""
    $md += "Respuesta:"
    $md += ""
    if ([string]::IsNullOrWhiteSpace($p.Cuerpo)) {
        $md += "_(sin cuerpo)_"
    }
    else {
        $bonito = $p.Cuerpo
        try {
            $datos = ConvertFrom-Json -InputObject $p.Cuerpo
            if ($p.Cuerpo.TrimStart().StartsWith("[")) {
                # Es una lista: se fuerza a arreglo para no perder los corchetes
                $items  = @(@($datos) | ForEach-Object { $_ })
                $bonito = ConvertTo-Json -InputObject $items -Depth 5
            }
            else {
                $bonito = ConvertTo-Json -InputObject $datos -Depth 5
            }
        } catch { }
        $md += $fence + "json"
        $md += $bonito
        $md += $fence
    }
}

$md | Out-File -FilePath $archivo -Encoding utf8

Write-Host ""
$colorFinal = if ($aprobadas -eq $total) { "Green" } else { "Yellow" }
Write-Host "  $aprobadas de $total pruebas aprobadas" -ForegroundColor $colorFinal
Write-Host "  Evidencia guardada en: $archivo"
Write-Host ""
