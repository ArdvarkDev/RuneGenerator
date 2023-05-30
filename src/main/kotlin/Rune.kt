import kotlin.random.Random

class Rune {

    companion object {
        private const val MIN_WEIGHT = 12
        private const val MAX_WEIGHT = 22
        private const val MIN_DOTS = 0
        private const val MAX_DOTS = 3
        private const val DOTS_NEED_NEIGHBOURS = true
        private const val WIDTH_CHECK = true
        private const val HEIGHT_CHECK = true
        private const val LINE_CHECK = true
        private const val NO_LINE_CHECK_IF_DOT = false
        private const val NO_LINE_CHECK_IF_NO_DOT = true
        const val WIDTH = 5
        const val HEIGHT = 7

        private var r = Random(0)
        private val previous = ArrayList<BooleanArray>()

        fun setSeed(seed: Int) {
            r = Random(seed)
        }
    }

    val bitmap = BooleanArray(WIDTH * HEIGHT)
    private var weight = 0

    init {

        do {
            generate()
            calculateWeight()
        } while (weight < MIN_WEIGHT || weight > MAX_WEIGHT || !isValid() || previous.contains(bitmap))

        previous.add(bitmap)
    }

    private fun generate() {
        // "seed dots" on first row
        setAt(0, 0, coin(r.nextDouble()))
        setAt(2, 0, coin(r.nextDouble()))
        setAt(4, 0, coin(r.nextDouble()))

        // "seed dots" on third row
        setAt(0, 3, coin(r.nextDouble()))
        setAt(2, 3, coin(r.nextDouble()))
        setAt(4, 3, coin(r.nextDouble()))

        // "seed dots" on sixth row
        setAt(0, 6, coin(r.nextDouble()))
        setAt(2, 6, coin(r.nextDouble()))
        setAt(4, 6, coin(r.nextDouble()))

        // in-betweens on the first row (dependent on first row seeds) (row check)
        setAt(1, 0, (getAt(0, 0) && getAt(2, 0) && coin(r.nextDouble())))
        setAt(3, 0, (getAt(2, 0) && getAt(4, 0) && coin(r.nextDouble())))

        // "seed dots" on second row (dependent on first and third rows) (column check)
        setAt(0, 1, (getAt(0, 0) && getAt(0, 3) && coin(r.nextDouble())))
        setAt(2, 1, (getAt(2, 0) && getAt(2, 3) && coin(r.nextDouble())))
        setAt(4, 1, (getAt(4, 0) && getAt(4, 3) && coin(r.nextDouble())))

        // use second row seed dots to fill in third row (creates lines between first and third rows)
        setAt(0, 2, getAt(0, 1) && coin(r.nextDouble()))
        setAt(2, 2, getAt(2, 1) && coin(r.nextDouble()))
        setAt(4, 2, getAt(4, 1) && coin(r.nextDouble()))

        // in-betweens on third row (dependent on third row) (row check)
        setAt(1, 3, getAt(0, 3) && getAt(2, 3) && coin(r.nextDouble()))
        setAt(3, 3, getAt(2, 3) && getAt(4, 3) && coin(r.nextDouble()))

        // "seed dots" on fourth row (dependent on third and sixth row) (column check)
        setAt(0, 4, (getAt(0, 3) && getAt(0, 6) && coin(r.nextDouble())))
        setAt(2, 4, (getAt(2, 3) && getAt(2, 6) && coin(r.nextDouble())))
        setAt(4, 4, (getAt(4, 3) && getAt(4, 6) && coin(r.nextDouble())))

        // use fourth row to fill in fifth row (creates lines between third and sixth rows)
        setAt(0, 5, getAt(0, 4) && coin(r.nextDouble()))
        setAt(2, 5, getAt(2, 4) && coin(r.nextDouble()))
        setAt(4, 5, getAt(4, 4) && coin(r.nextDouble()))

        // in-betweens on sixth row (dependent on sixth row) (row check)
        setAt(1, 6, getAt(0, 6) && getAt(2, 6) && coin(r.nextDouble()))
        setAt(3, 6, getAt(2, 6) && getAt(4, 6) && coin(r.nextDouble()))
    }

    private fun calculateWeight() {
        weight = 0
        for (value in bitmap)
            weight += if (value) 1 else 0
    }

    private fun isValid(): Boolean {

        if (WIDTH_CHECK) {
            var left = false
            var right = false
            for (i in 0 until HEIGHT) {
                left = left || getAt(0, i)
                right = right || getAt(WIDTH - 1, i)
            }
            if (!left || !right)
                return false
        }

        if (HEIGHT_CHECK) {
            var top = false
            var bottom = false
            for (i in 0 until WIDTH) {
                top = top || getAt(i, 0)
                bottom = bottom || getAt(i, HEIGHT - 1)
            }
            if (!top || !bottom)
                return false
        }

        val dots = ArrayList<Pair<Int, Int>>()

        for (x in 0 until WIDTH) {
            for (y in 0 until HEIGHT) {
                if (getAt(x, y) && countNeighbours(x, y) == 0) {
                    // found a dot

                    if (dots.size < MAX_DOTS) {
                        if (DOTS_NEED_NEIGHBOURS)
                            if (countNeighbours(x, y, 2) == 0)
                                // dot is too far away from other stuff
                                return false

                        dots.add(Pair(x, y))
                    } else
                        // there is more than one dot
                        return false
                }
            }
        }

        if (dots.size < MIN_DOTS)
            return false

        if (NO_LINE_CHECK_IF_DOT)
            if (dots.isNotEmpty())
                return true

        if (NO_LINE_CHECK_IF_NO_DOT)
            if (dots.isEmpty())
                return true

        if (LINE_CHECK) {
            var pointX = -1
            var pointY = -1

            for (x in 0 until WIDTH) {
                for (y in 0 until HEIGHT) {
                    if (getAt(x, y) && !dots.contains(Pair(x, y))) {
                        pointX = x
                        pointY = y
                        break
                    }
                }
            }


            val checked = ArrayList<Int>()

            fun fill(x: Int, y: Int): Int {
                val index = y * WIDTH + x
                if (!getAt(x, y) || checked.contains(index))
                    return 0

                checked.add(index)

                var a = 1
                if (x > 0) a += fill(x - 1, y)
                if (x < WIDTH - 1) a += fill(x + 1, y)
                if (y > 0) a += fill(x, y - 1)
                if (y < HEIGHT - 1) a += fill(x, y + 1)

                return a
            }

            val a = fill(pointX, pointY)

            return a == weight || a + dots.size == weight
        }

        return true
    }

    private fun countNeighbours(x: Int, y: Int, r: Int = 1): Int {
        var count = 0

        if (x > 0+r && getAt(x-r, y)) count++
        if (x < WIDTH-r && getAt(x+r, y)) count++
        if (y > 0+r && getAt(x, y-r)) count++
        if (y < HEIGHT-r && getAt(x, y+r)) count++

        return count
    }

    private fun getAt(x: Int, y: Int): Boolean {
        return bitmap[y * WIDTH + x]
    }

    private fun setAt(x: Int, y: Int, value: Boolean) {
        bitmap[y * WIDTH + x] = value
    }

    private fun coin(chance: Double = 0.5): Boolean {
        return r.nextDouble() < chance
    }
}