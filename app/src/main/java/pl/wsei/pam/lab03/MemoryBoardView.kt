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
    private val rows: Int,
    private val savedIcons: Array<Int>? = null,
    private val savedRevealed: IntArray? = null
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
        R.drawable.baseline_email_24,
        R.drawable.baseline_favorite_24,
        R.drawable.baseline_home_24,
        R.drawable.baseline_settings_24,
        R.drawable.baseline_camera_24,
        R.drawable.baseline_cloud_24,
        R.drawable.baseline_bluetooth_24,
        R.drawable.baseline_location_on_24,
        R.drawable.baseline_wifi_24
    )

    private val deckResource: Int = R.drawable.bg_button
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { _ -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        val iconList: MutableList<Int> = if (savedIcons != null) {
            savedIcons.toMutableList()
        } else {
            mutableListOf<Int>().apply {
                addAll(icons.shuffled().take(cols * rows / 2).flatMap { listOf(it, it) })
                shuffle()
            }
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
                    tag = "$row-$col"
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                }

                gridLayout.addView(button)
                val iconRes = iconList.removeFirst()
                val tile = Tile(button, iconRes, deckResource)

                if (savedRevealed != null) {
                    val index = row * cols + col
                    tile.revealed = savedRevealed.getOrNull(index) == iconRes
                }

                button.setOnClickListener(::onClickTile)
                tiles[button.tag.toString()] = tile
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
        
        // Ignoruj kliknięcie jeśli karta jest już odkryta
        if (tile?.revealed == true) {
            return
        }
        
        tile?.revealed = true
        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource ?: -1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        
        // Wyłącz słuchacza kliknięć dla dopasowanych kart
        if (matchResult == GameStates.Match) {
            matchedPair.forEach { it.button.setOnClickListener(null) }
        }
        
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    fun getState(): IntArray {
        return tiles.values.map {
            if (it.revealed) it.tileResource else -1
        }.toIntArray()
    }

    fun getFullState(): Array<Int> {
        return tiles.values.map { it.tileResource }.toTypedArray()
    }
}
