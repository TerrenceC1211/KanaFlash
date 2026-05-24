$ErrorActionPreference = "Stop"

function RgbColor([int]$r, [int]$g, [int]$b) {
    return $r + ($g * 256) + ($b * 65536)
}

function Set-TextStyle($textRange, [string]$fontName, [int]$fontSize, [int]$color, [bool]$bold = $false) {
    $textRange.Font.Name = $fontName
    $textRange.Font.Size = $fontSize
    $textRange.Font.Bold = if ($bold) { -1 } else { 0 }
    $textRange.Font.Color.RGB = $color
}

function Add-Textbox($slide, [string]$text, [double]$left, [double]$top, [double]$width, [double]$height, [string]$fontName, [int]$fontSize, [int]$color, [bool]$bold = $false) {
    $shape = $slide.Shapes.AddTextbox(1, $left, $top, $width, $height)
    $shape.TextFrame.TextRange.Text = $text
    Set-TextStyle $shape.TextFrame.TextRange $fontName $fontSize $color $bold
    $shape.TextFrame.MarginLeft = 0
    $shape.TextFrame.MarginRight = 0
    $shape.TextFrame.MarginTop = 0
    $shape.TextFrame.MarginBottom = 0
    return $shape
}

function Add-RoundedCard($slide, [double]$left, [double]$top, [double]$width, [double]$height, [int]$fillColor, [double]$transparency = 0.0) {
    $shape = $slide.Shapes.AddShape(5, $left, $top, $width, $height)
    $shape.Fill.ForeColor.RGB = $fillColor
    $shape.Fill.Transparency = $transparency
    $shape.Line.Visible = 0
    return $shape
}

function Add-Pill($slide, [string]$text, [double]$left, [double]$top, [double]$width, [double]$height, [int]$fillColor, [int]$textColor) {
    $pill = Add-RoundedCard $slide $left $top $width $height $fillColor 0.0
    $pill.TextFrame.TextRange.Text = $text
    Set-TextStyle $pill.TextFrame.TextRange "Aptos" 15 $textColor $true
    $pill.TextFrame.HorizontalAnchor = 2
    $pill.TextFrame.VerticalAnchor = 3
    $pill.TextFrame.MarginLeft = 12
    $pill.TextFrame.MarginRight = 12
    return $pill
}

function Add-BulletList($slide, [string[]]$items, [double]$left, [double]$top, [double]$width, [double]$height, [int]$color) {
    $shape = $slide.Shapes.AddTextbox(1, $left, $top, $width, $height)
    $shape.TextFrame.TextRange.Text = ($items -join "`r")
    $shape.TextFrame.WordWrap = -1
    $shape.TextFrame.MarginLeft = 0
    $shape.TextFrame.MarginRight = 0
    $shape.TextFrame.MarginTop = 0
    $shape.TextFrame.MarginBottom = 0

    for ($i = 1; $i -le $shape.TextFrame.TextRange.Paragraphs().Count; $i++) {
        $paragraph = $shape.TextFrame.TextRange.Paragraphs($i)
        Set-TextStyle $paragraph "Aptos" 20 $color $false
        $paragraph.ParagraphFormat.Bullet.Visible = -1
        $paragraph.ParagraphFormat.Bullet.Character = 8226
        $paragraph.ParagraphFormat.Bullet.RelativeSize = 0.9
        $paragraph.ParagraphFormat.SpaceAfter = 9
    }

    return $shape
}

function Add-AccentLines($slide, [int]$lineColor, [int]$dotColor) {
    $line1 = $slide.Shapes.AddLine(52, 200, 420, 200)
    $line1.Line.ForeColor.RGB = $lineColor
    $line1.Line.Transparency = 0.82
    $line1.Line.Weight = 1.4

    $line2 = $slide.Shapes.AddLine(860, 78, 1260, 78)
    $line2.Line.ForeColor.RGB = $lineColor
    $line2.Line.Transparency = 0.8
    $line2.Line.Weight = 1.2

    $line3 = $slide.Shapes.AddLine(940, 608, 1230, 608)
    $line3.Line.ForeColor.RGB = $lineColor
    $line3.Line.Transparency = 0.8
    $line3.Line.Weight = 1.2

    foreach ($coords in @(@(404, 200), @(1020, 78), @(1110, 608))) {
        $dot = $slide.Shapes.AddShape(9, $coords[0], $coords[1] - 4, 8, 8)
        $dot.Fill.ForeColor.RGB = $dotColor
        $dot.Line.Visible = 0
    }
}

function Add-SlideBase($slide, [int]$bgColor, [int]$softColor, [int]$accentColor) {
    $slide.FollowMasterBackground = 0
    $slide.Background.Fill.ForeColor.RGB = $bgColor

    $glow1 = $slide.Shapes.AddShape(9, 910, -110, 380, 380)
    $glow1.Fill.ForeColor.RGB = $softColor
    $glow1.Fill.Transparency = 0.62
    $glow1.Line.Visible = 0

    $glow2 = $slide.Shapes.AddShape(9, -120, 470, 300, 300)
    $glow2.Fill.ForeColor.RGB = $accentColor
    $glow2.Fill.Transparency = 0.9
    $glow2.Line.Visible = 0

    Add-AccentLines $slide $softColor $accentColor | Out-Null
}

function Add-SectionTitle($slide, [string]$eyebrow, [string]$title, [string]$subtitle, [int]$accentColor, [int]$titleColor, [int]$bodyColor) {
    Add-Textbox $slide $eyebrow.ToUpperInvariant() 84 56 240 26 "Aptos" 16 $accentColor $true | Out-Null

    Add-Textbox $slide $title 84 88 640 68 "Aptos Display" 28 $titleColor $true | Out-Null
    Add-Textbox $slide $subtitle 84 150 640 42 "Aptos" 16 $bodyColor $false | Out-Null
}

function Add-PhoneFrame($slide, [double]$left, [double]$top, [double]$width, [double]$height, [int]$frameColor, [int]$screenColor) {
    $frame = $slide.Shapes.AddShape(5, $left, $top, $width, $height)
    $frame.Fill.ForeColor.RGB = $frameColor
    $frame.Line.Visible = 0

    $screen = $slide.Shapes.AddShape(1, $left + 14, $top + 16, $width - 28, $height - 32)
    $screen.Fill.ForeColor.RGB = $screenColor
    $screen.Line.Visible = 0

    $speaker = $slide.Shapes.AddShape(5, $left + ($width / 2) - 28, $top + 10, 56, 7)
    $speaker.Fill.ForeColor.RGB = $screenColor
    $speaker.Line.Visible = 0
}

$repoRoot = Split-Path -Parent $PSScriptRoot
$outputDir = Join-Path $repoRoot "output\slides"
$assetsDir = Join-Path $outputDir "assets"
$pptxPath = Join-Path $outputDir "KanaFlash_Presentation.pptx"
$pdfPath = Join-Path $outputDir "KanaFlash_Presentation.pdf"

New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
New-Item -ItemType Directory -Force -Path $assetsDir | Out-Null

$logoSource = Join-Path $repoRoot "app\src\main\res\drawable\logo.png"
$logoTarget = Join-Path $assetsDir "logo.png"
Copy-Item -LiteralPath $logoSource -Destination $logoTarget -Force

$paper = RgbColor 255 251 245
$forest = RgbColor 79 111 82
$sage = RgbColor 115 144 114
$terracotta = RgbColor 210 125 95
$softCream = RgbColor 244 239 230
$mist = RgbColor 229 228 221
$ink = RgbColor 31 42 36
$softInk = RgbColor 95 106 99
$white = RgbColor 255 255 255
$deepForest = RgbColor 36 52 42

$msoTrue = -1
$ppLayoutBlank = 12
$ppSaveAsPDF = 32

$ppt = New-Object -ComObject PowerPoint.Application
$ppt.Visible = $msoTrue
$presentation = $ppt.Presentations.Add()
$presentation.PageSetup.SlideSize = 16

try {
    $slide1 = $presentation.Slides.Add(1, $ppLayoutBlank)
    Add-SlideBase $slide1 $paper $mist $sage

    $hero = Add-RoundedCard $slide1 72 96 610 454 $white 0.0
    $hero.Shadow.Visible = $msoTrue
    $hero.Shadow.Blur = 18
    $hero.Shadow.Transparency = 0.78
    $hero.Shadow.ForeColor.RGB = $deepForest

    Add-Textbox $slide1 "Mobile App Project Presentation" 104 104 280 26 "Aptos" 15 $sage $true | Out-Null
    $title = Add-Textbox $slide1 "KanaFlash" 104 136 320 62 "Aptos Display" 28 $ink $true
    $title.TextFrame.TextRange.Font.Size = 30
    Add-Textbox $slide1 "A clean study companion for learning Japanese vocabulary through decks, flashcards, quizzes, and writing practice." 104 206 480 74 "Aptos" 19 $softInk $false | Out-Null

    Add-Pill $slide1 "Jetpack Compose UI" 104 316 160 36 $softCream $forest | Out-Null
    Add-Pill $slide1 "Room Database" 276 316 136 36 $softCream $forest | Out-Null
    Add-Pill $slide1 "Touch Writing Pad" 424 316 156 36 $softCream $forest | Out-Null

    $summary = Add-RoundedCard $slide1 104 380 522 122 $softCream 0.0
    $summary.Fill.ForeColor.RGB = $softCream
    Add-Textbox $slide1 "Presentation flow" 130 402 180 22 "Aptos" 16 $forest $true | Out-Null
    Add-Textbox $slide1 "Introduction - Demo - Technical implementation - Challenges faced - Further improvement" 130 436 450 42 "Aptos" 17 $softInk $false | Out-Null

    Add-PhoneFrame $slide1 826 102 280 474 $deepForest $softCream
    $logoPic = $slide1.Shapes.AddPicture($logoTarget, 0, $msoTrue, 858, 172, 220, 150)
    $logoPic.LockAspectRatio = $msoTrue

    $screenCard1 = Add-RoundedCard $slide1 854 336 224 74 $white 0.0
    Add-Textbox $slide1 "Deck-based study" 876 352 140 20 "Aptos" 16 $ink $true | Out-Null
    Add-Textbox $slide1 "Organize words and practice in small sessions." 876 376 170 32 "Aptos" 13 $softInk $false | Out-Null

    $screenCard2 = Add-RoundedCard $slide1 854 426 224 96 $forest 0.0
    Add-Textbox $slide1 "3 learning modes" 876 446 140 20 "Aptos" 16 $white $true | Out-Null
    Add-Textbox $slide1 "Flashcards`rQuiz`rWrite practice" 876 472 150 42 "Aptos" 14 $paper $false | Out-Null

    $slide2 = $presentation.Slides.Add(2, $ppLayoutBlank)
    Add-SlideBase $slide2 $paper $mist $sage
    Add-SectionTitle $slide2 "01  Introduction" "Project overview" "A quick introduction to what the app solves and the experience it provides." $sage $ink $softInk

    $leftCard = Add-RoundedCard $slide2 78 212 560 366 $white 0.0
    $leftCard.Shadow.Visible = $msoTrue
    $leftCard.Shadow.Blur = 12
    $leftCard.Shadow.Transparency = 0.84
    Add-Textbox $slide2 "What KanaFlash is" 108 242 220 26 "Aptos" 18 $forest $true | Out-Null
    Add-BulletList $slide2 @(
        "Android mobile app for learning Japanese vocabulary in a simple, focused flow",
        "Lets users create decks and save romaji, hiragana, and meaning for each word",
        "Supports quick review through flashcards, quizzes, and handwriting practice"
    ) 108 284 486 220 $ink | Out-Null

    $rightCard = Add-RoundedCard $slide2 674 212 506 366 $softCream 0.0
    Add-Textbox $slide2 "Key user experience" 706 242 220 26 "Aptos" 18 $forest $true | Out-Null

    foreach ($item in @(
        @{ Title = "Deck management"; Body = "Separate vocabulary into custom decks for targeted study."; Top = 286 },
        @{ Title = "Guided review"; Body = "Switch between preview, reveal, multiple-choice, and writing tasks."; Top = 382 },
        @{ Title = "Clean interface"; Body = "Rounded cards, soft colors, and simple navigation keep learning readable."; Top = 478 }
    )) {
        $mini = Add-RoundedCard $slide2 706 $item.Top 438 76 $white 0.0
        Add-Textbox $slide2 $item.Title 728 ($item.Top + 16) 190 20 "Aptos" 16 $ink $true | Out-Null
        Add-Textbox $slide2 $item.Body 728 ($item.Top + 38) 380 26 "Aptos" 13 $softInk $false | Out-Null
    }

    $slide3 = $presentation.Slides.Add(3, $ppLayoutBlank)
    Add-SlideBase $slide3 $deepForest $sage $terracotta

    $sectionBand = Add-RoundedCard $slide3 86 170 1110 340 $paper 0.0
    $sectionBand.Fill.Transparency = 0.04
    Add-Textbox $slide3 "02  Demo" 132 228 220 24 "Aptos" 18 $sage $true | Out-Null
    $demoTitle = Add-Textbox $slide3 "Live walkthrough" 128 270 520 72 "Aptos Display" 34 $ink $true
    $demoTitle.TextFrame.TextRange.Font.Size = 36
    Add-Textbox $slide3 "Section divider only.`rUse this slide before demonstrating the application." 132 356 460 64 "Aptos" 19 $softInk $false | Out-Null

    $demoPhone = $slide3.Shapes.AddShape(5, 818, 146, 250, 410)
    $demoPhone.Fill.ForeColor.RGB = $white
    $demoPhone.Fill.Transparency = 0.06
    $demoPhone.Line.ForeColor.RGB = $paper
    $demoPhone.Line.Transparency = 0.55
    $demoPhone.Line.Weight = 1.2

    foreach ($t in @(206, 278, 350, 422)) {
        $box = $slide3.Shapes.AddShape(5, 850, $t, 186, 44)
        $box.Fill.ForeColor.RGB = $paper
        $box.Fill.Transparency = 0.18
        $box.Line.Visible = 0
    }

    $slide4 = $presentation.Slides.Add(4, $ppLayoutBlank)
    Add-SlideBase $slide4 $paper $mist $sage
    Add-SectionTitle $slide4 "03  Technical Implementation" "How the app is built" "The app uses a straightforward Android architecture designed for local-first study flows." $sage $ink $softInk

    $archCard = Add-RoundedCard $slide4 76 212 540 372 $white 0.0
    Add-Textbox $slide4 "Architecture" 104 240 180 24 "Aptos" 18 $forest $true | Out-Null
    Add-BulletList $slide4 @(
        "Kotlin Android app with Jetpack Compose for the full UI layer",
        "Navigation Compose manages screen-to-screen flow across home, decks, and learning modes",
        "ViewModels keep study state stable while Repository + Room handle local data"
    ) 104 278 470 210 $ink | Out-Null

    $stackStrip = Add-RoundedCard $slide4 104 476 462 70 $softCream 0.0
    Add-Textbox $slide4 "Compose UI   →   ViewModel   →   Repository   →   Room Database" 126 498 416 24 "Aptos" 16 $forest $true | Out-Null

    $featureCard = Add-RoundedCard $slide4 650 212 544 372 $softCream 0.0
    Add-Textbox $slide4 "Implementation highlights" 680 240 230 24 "Aptos" 18 $forest $true | Out-Null
    Add-BulletList $slide4 @(
        "Deck selection is shared across flashcard, quiz, and write practice screens",
        "Quiz mode generates answer options dynamically from saved vocabulary",
        "Write mode uses gesture input, custom canvas strokes, undo, clear, and answer reveal"
    ) 680 278 468 214 $ink | Out-Null

    $dbCard = Add-RoundedCard $slide4 680 490 468 56 $white 0.0
    Add-Textbox $slide4 "Database model: one Deck can contain many Vocabulary Entries, with cascade delete enabled." 700 508 430 20 "Aptos" 13 $softInk $false | Out-Null

    $slide5 = $presentation.Slides.Add(5, $ppLayoutBlank)
    Add-SlideBase $slide5 $paper $mist $terracotta
    Add-SectionTitle $slide5 "04  Challenges Faced" "Main development challenges" "The project required balancing clean UX with interactive study features and local persistence." $terracotta $ink $softInk

    foreach ($card in @(
        @{ Left = 82; Title = "State management"; Body = "Keeping deck choice, question progress, and reveal states consistent across multiple study modes."; Color = $white },
        @{ Left = 416; Title = "Interactive writing"; Body = "Building a smooth handwriting canvas with drag gestures, stroke storage, undo, and clear actions."; Color = $softCream },
        @{ Left = 750; Title = "Data structure changes"; Body = "Migrating from a simpler vocabulary model to multi-deck storage while preserving app behavior."; Color = $white }
    )) {
        $panel = Add-RoundedCard $slide5 $card.Left 244 284 258 $card.Color 0.0
        $panel.Shadow.Visible = $msoTrue
        $panel.Shadow.Blur = 10
        $panel.Shadow.Transparency = 0.86
        Add-Textbox $slide5 $card.Title ($card.Left + 24) 274 220 46 "Aptos Display" 21 $ink $true | Out-Null
        Add-Textbox $slide5 $card.Body ($card.Left + 24) 352 220 104 "Aptos" 16 $softInk $false | Out-Null
    }

    $footerTag = Add-RoundedCard $slide5 84 546 1094 42 $softCream 0.0
    Add-Textbox $slide5 "Challenge theme: keeping the app simple for the user while the underlying logic became more feature-rich." 112 558 1030 18 "Aptos" 13 $forest $false | Out-Null

    $slide6 = $presentation.Slides.Add(6, $ppLayoutBlank)
    Add-SlideBase $slide6 $paper $mist $sage
    Add-SectionTitle $slide6 "05  Further Improvement" "Next steps for the app" "Possible enhancements that would strengthen learning value, usability, and long-term scalability." $sage $ink $softInk

    foreach ($idea in @(
        @{ Left = 82; Top = 236; Title = "Smarter revision"; Body = "Add spaced repetition or streak-based review scheduling."; Accent = $sage },
        @{ Left = 430; Top = 236; Title = "Better writing feedback"; Body = "Compare user strokes with expected kana for instant guidance."; Accent = $terracotta },
        @{ Left = 778; Top = 236; Title = "Progress analytics"; Body = "Show quiz trends, weak words, and deck-level learning summaries."; Accent = $sage },
        @{ Left = 256; Top = 430; Title = "Cloud backup"; Body = "Allow sync across devices and safer data recovery."; Accent = $terracotta },
        @{ Left = 604; Top = 430; Title = "Testing and polish"; Body = "Expand automated tests and add more accessibility refinements."; Accent = $sage }
    )) {
        $ideaCard = Add-RoundedCard $slide6 $idea.Left $idea.Top 282 118 $white 0.0
        $ideaCard.Shadow.Visible = $msoTrue
        $ideaCard.Shadow.Blur = 10
        $ideaCard.Shadow.Transparency = 0.88
        $accent = $slide6.Shapes.AddShape(1, $idea.Left, $idea.Top, 282, 8)
        $accent.Fill.ForeColor.RGB = $idea.Accent
        $accent.Line.Visible = 0
        Add-Textbox $slide6 $idea.Title ($idea.Left + 20) ($idea.Top + 24) 220 22 "Aptos" 16 $ink $true | Out-Null
        Add-Textbox $slide6 $idea.Body ($idea.Left + 20) ($idea.Top + 52) 236 44 "Aptos" 13 $softInk $false | Out-Null
    }

    if (Test-Path $pptxPath) {
        Remove-Item -LiteralPath $pptxPath -Force
    }
    if (Test-Path $pdfPath) {
        Remove-Item -LiteralPath $pdfPath -Force
    }

    $presentation.SaveAs($pptxPath)
    $presentation.SaveAs($pdfPath, $ppSaveAsPDF)
}
finally {
    $presentation.Close()
    $ppt.Quit()
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($presentation) | Out-Null
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($ppt) | Out-Null
    [System.GC]::Collect()
    [System.GC]::WaitForPendingFinalizers()
}
