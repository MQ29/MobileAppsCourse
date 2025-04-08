package pl.wsei.pam.lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab02)

        // Ustawienie listenerów dla przycisków
        findViewById<Button>(R.id.main_6_6_board).setOnClickListener { onBoardSizeSelected(it) }
        findViewById<Button>(R.id.main_4_4_board).setOnClickListener { onBoardSizeSelected(it) }
        findViewById<Button>(R.id.main_4_3_board).setOnClickListener { onBoardSizeSelected(it) }
        findViewById<Button>(R.id.main_3_2_board).setOnClickListener { onBoardSizeSelected(it) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favorites_grid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Funkcja obsługująca kliknięcie przycisku
    fun onBoardSizeSelected(view: View) {
        val tag: String? = view.tag as String?
        val tokens: List<String>? = tag?.split(" ")
        val rows = tokens?.get(0)?.toInt() ?: 3
        val columns = tokens?.get(1)?.toInt() ?: 3

        val intent = Intent(this, Lab03Activity::class.java)
        intent.putExtra("size", intArrayOf(rows, columns))
        startActivity(intent)
    }
}