package pl.wsei.pam.lab03

import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import pl.wsei.pam.lab01.R
import java.util.*

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()

    private val icons: List<Int> = listOf(
        R.drawable.baseline_android_24,
        R.drawable.baseline_audiotrack_24,
        R.drawable.baseline_bug_report_24,
        R.drawable.baseline_heart_broken_24,
        R.drawable.baseline_lightbulb_24,
        R.drawable.baseline_rocket_24,
        R.drawable.baseline_star_24,
        R.drawable.baseline_laptop_windows_24,
        R.drawable.baseline_language_24,
        R.drawable.baseline_audiotrack_24,
        R.drawable.baseline_audiotrack_24,
        R.drawable.baseline_audiotrack_24
    )

    private val deckResource: Int = R.drawable.bg_button

    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { _ -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = ImageButton(gridLayout.context).apply {
                    val layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1, 1f)
                        rowSpec = GridLayout.spec(row, 1, 1f)
                    }
                    this.layoutParams = layoutParams
                    tag = "$row$col"
                    scaleType = ImageView.ScaleType.CENTER_INSIDE

                }
                gridLayout.addView(button)
                val iconRes = shuffledIcons.removeFirst()
                addTile(button, iconRes)
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
        tile?.revealed = true
        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource ?: -1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }

    fun getState(): IntArray {
        return tiles.values.map {
            if (it.revealed) it.tileResource else -1
        }.toIntArray()
    }

    fun setState(state: IntArray) {
        val revealedTiles = state.withIndex()
            .filter { it.value != -1 }
            .associate { it.index to it.value }

        val keys = tiles.keys.sorted() // 00, 01, 02...
        for ((index, key) in keys.withIndex()) {
            val tile = tiles[key]
            if (tile != null) {
                tile.revealed = revealedTiles.containsKey(index)
            }
        }
    }
}
