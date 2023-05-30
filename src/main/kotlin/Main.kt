import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private const val WIDTH = 10
private const val HEIGHT = 10
private const val SPACING = 4
private const val BACKGROUND = 0
private const val RUNE_BACKGROUND = 0
private const val RUNE_FOREGROUND = 255

fun main() {

    val bufImg = BufferedImage(WIDTH * (Rune.WIDTH + SPACING) - SPACING, HEIGHT * (Rune.HEIGHT + SPACING) - SPACING, BufferedImage.TYPE_INT_ARGB)
    for (y in 0 until bufImg.height)
        for (x in 0 until bufImg.width)
            bufImg.setRGB(x, y, 255.shl(24) or BACKGROUND.shl(16) or BACKGROUND.shl(8) or BACKGROUND)

    Rune.setSeed((Math.random() * 100000).toInt())

    for (y in 0 until HEIGHT) {
        for (x in 0 until WIDTH) {
            val rune = Rune()

            for (i in rune.bitmap.indices) {
                val colour = if (rune.bitmap[i]) RUNE_FOREGROUND else RUNE_BACKGROUND
                val argb = 255.shl(24) or colour.shl(16) or colour.shl(8) or colour
                bufImg.setRGB(x * (Rune.WIDTH+SPACING) + i % Rune.WIDTH, y * (Rune.HEIGHT+SPACING) + i / Rune.WIDTH, argb)
            }
        }
    }

    ImageIO.write(bufImg, "png", File("result.png"))
}
