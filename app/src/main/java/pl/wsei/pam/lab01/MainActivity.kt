package pl.wsei.pam.lab01

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab02.Lab02Activity
import pl.wsei.pam.lab06.Lab06Activity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    fun onClickMainBtnRunLab01(v: View){
        val intent = Intent(this, Lab01Activity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }
    fun onClickMainBtnRunLab02(v: View){
        val intent = Intent(this, Lab02Activity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }
    
    fun onClickMainBtnRunLab06(v: View){
        val intent = Intent(this, Lab06Activity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Running Lab06", Toast.LENGTH_SHORT).show()
    }
}