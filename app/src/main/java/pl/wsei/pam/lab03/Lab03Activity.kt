package pl.wsei.pam.lab03

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import java.util.*
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {

    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        val rows = size[0]
        val columns = size[1]

        mBoard = findViewById(R.id.memory_grid)
        mBoard.rowCount = rows
        mBoard.columnCount = columns

        mBoardModel = if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("board_state")
            MemoryBoardView(mBoard, columns, rows).apply {
                if (savedState != null) setState(savedState)
            }
        } else {
            MemoryBoardView(mBoard, columns, rows)
        }

        mBoardModel.setOnGameChangeListener { e ->
            when (e.state) {
                GameStates.Matching -> {
                    e.tiles.forEach { it.revealed = true }
                }
                GameStates.Match -> {
                    e.tiles.forEach { it.revealed = true }
                }
                GameStates.NoMatch -> {
                    e.tiles.forEach { it.revealed = true }
                    Timer().schedule(2000) {
                        runOnUiThread {
                            e.tiles.forEach { it.revealed = false }
                        }
                    }
                }
                GameStates.Finished -> {
                    Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("board_state", mBoardModel.getState())
    }
}
