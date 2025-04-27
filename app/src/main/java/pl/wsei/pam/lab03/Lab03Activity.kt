package pl.wsei.pam.lab03

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import java.util.*
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {

    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView
    private lateinit var completionPlayer: MediaPlayer
    private lateinit var wrongPlayer: MediaPlayer
    private var isSound = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        val rows = size[0]
        val columns = size[1]

        mBoard = findViewById(R.id.memory_grid)
        mBoard.rowCount = rows
        mBoard.columnCount = columns


        val savedIcons = savedInstanceState?.getSerializable("tile_resources") as? Array<Int>
        val savedRevealed = savedInstanceState?.getIntArray("revealed_tiles")

        mBoardModel = if (savedIcons != null && savedRevealed != null) {
            MemoryBoardView(mBoard, columns, rows, savedIcons, savedRevealed)
        } else {
            MemoryBoardView(mBoard, columns, rows)
        }

        mBoardModel.setOnGameChangeListener { e ->
            when (e.state) {
                GameStates.Matching -> e.tiles.forEach { it.revealed = true }
                GameStates.Match -> {
                    if (isSound) completionPlayer.start()
                    e.tiles.forEach { it.revealed = true }
                    e.tiles.forEach { tile ->
                        animatePairedButton(tile.button, Runnable {})
                    }
                }
                GameStates.NoMatch -> {
                    if (isSound) wrongPlayer.start()
                    e.tiles.forEach { it.revealed = true }
                    e.tiles.forEach { tile ->
                        animateWrongPairButton(tile.button, Runnable {
                            tile.revealed = false
                        })
                    }
                }
                GameStates.Finished -> {
                    Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()
        val random = Random()
        button.pivotX = random.nextFloat() * 200f
        button.pivotY = random.nextFloat() * 200f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)

        set.startDelay = 500
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scaleX, scaleY, fade)

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 1f
                action.run()
            }
        })

        set.start()
    }

    private fun animateWrongPairButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()

        val rotateLeft = ObjectAnimator.ofFloat(button, "rotation", 0f, -15f)
        val rotateRight = ObjectAnimator.ofFloat(button, "rotation", -15f, 15f)
        val rotateBack = ObjectAnimator.ofFloat(button, "rotation", 15f, 0f)

        set.playSequentially(rotateLeft, rotateRight, rotateBack)
        set.duration = 300
        set.interpolator = DecelerateInterpolator()

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                button.rotation = 0f
                action.run()
            }
        })

        set.start()
    }

    override fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        wrongPlayer = MediaPlayer.create(applicationContext, R.raw.wrong)
    }

    override fun onPause() {
        super.onPause()
        completionPlayer.release()
        wrongPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("revealed_tiles", mBoardModel.getState())
        outState.putSerializable("tile_resources", mBoardModel.getFullState())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.board_activity_sound) {
            if (item.icon?.constantState == resources.getDrawable(R.drawable.baseline_volume_up_24, theme).constantState) {
                item.setIcon(R.drawable.baseline_volume_off_24)
                Toast.makeText(this, "Sound off", Toast.LENGTH_SHORT).show()
                isSound = false
            } else {
                item.setIcon(R.drawable.baseline_volume_up_24)
                Toast.makeText(this, "Sound on", Toast.LENGTH_SHORT).show()
                isSound = true
            }
        }
        return true
    }
}
