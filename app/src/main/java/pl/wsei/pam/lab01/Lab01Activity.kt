package pl.wsei.pam.lab01

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Lab01Activity : AppCompatActivity() {
    lateinit var mLayout: LinearLayout
    lateinit var mTitle: TextView
    lateinit var mProgress: ProgressBar
    var mBoxes: MutableList<CheckBox> = mutableListOf()
    var mButtons: MutableList<Button> = mutableListOf()

    var completedTasks = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab01)
        mLayout = findViewById(R.id.main)

        mTitle = TextView(this).also {
            it.text = "Laboratorium 1"
            it.textSize = 24f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(20, 20, 20, 20)
            }
            it.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            it.layoutParams = params
        }
        mLayout.addView(mTitle)

        mProgress = ProgressBar(
            this,
            null,
            androidx.appcompat.R.attr.progressBarStyle,
            androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal
        ).also {
            it.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            it.max = 100
            it.progress = 0
        }
        mLayout.addView(mProgress)

        val taskMap = mapOf(
            1 to {
                val result1 = task11(4, 6)
                val result2 = task11(7, -6)
                result1 in 0.666665..0.666667 && result2 in -1.1666667..-1.1666665
            },
            2 to {
                val result1 = task12(7u, 6u)
                val result2 = task12(12u, 15u)
                result1 == "7 + 6 = 13" && result2 == "12 + 15 = 27"
            },
            3 to {
                task13(0.0, 5.4f) && !task13(7.0, 5.4f) &&
                        !task13(-6.0, -1.0f) && task13(6.0, 9.1f) &&
                        !task13(6.0, -1.0f) && task13(1.0, 1.1f)
            },
            4 to {
                val result1 = task14(-2, 5)
                val result2 = task14(-2, -5)
                result1 == "-2 + 5 = 3" && result2 == "-2 - 5 = -7"
            },
            5 to {
                task15("DOBRY") == 4 && task15("barDzo dobry") == 5 &&
                        task15("doStateczny") == 3 && task15("Dopuszczający") == 2 &&
                        task15("NIEDOSTATECZNY") == 1 && task15("XYZ") == -1
            },
            6 to {
                task16(mapOf("A" to 2u, "B" to 4u, "C" to 3u), mapOf("A" to 1u, "B" to 2u)) == 2u &&
                        task16(mapOf("A" to 2u, "B" to 4u, "C" to 3u), mapOf("F" to 1u, "G" to 2u)) == 0u &&
                        task16(mapOf("A" to 23u, "B" to 47u, "C" to 30u), mapOf("A" to 1u, "B" to 2u, "C" to 4u)) == 7u
            }
        )

        for (i in 1..6) {
           var row = LinearLayout(this).also{
               it.layoutParams = LinearLayout.LayoutParams(
                   LinearLayout.LayoutParams.WRAP_CONTENT,
                   LinearLayout.LayoutParams.WRAP_CONTENT
               )
               it.orientation = LinearLayout.HORIZONTAL
           }

            val checkBox = CheckBox(this).also {
                it.text = "Zadanie ${i}"
                it.isEnabled = false
            }
            mBoxes.add(checkBox)

            val button = Button(this).also {
                it.text = "Testuj"
                it.isEnabled = true
                it.setOnClickListener {
                    val result = taskMap[i]?.invoke() ?: false
                    checkBox.isChecked = result
                    if (result && i !in completedTasks) {
                        completedTasks.add(i)
                        mProgress.progress += (100.0 / 6.0).toInt()
                        if(mProgress.progress == 96)
                            mProgress.progress = 100
                    }
                }
            }
            mButtons.add(button)

            row.addView(checkBox)
            row.addView(button)
            mLayout.addView(row)
        }
    }

    // Wykonaj dzielenie niecałkowite parametru a przez b
    // Wynik zwróć po instrukcji return
    private fun task11(a: Int, b: Int): Double {
        if (b == 0) throw IllegalArgumentException("Dzielenie przez zero!")
        val wynik = a.toDouble() / b
        return wynik
    }

    // Zdefiniuj funkcję, która zwraca łańcuch dla argumentów bez znaku (zawsze dodatnie) wg schematu
    // <a> + <b> = <a + b>
    // np. dla parametrów a = 2 i b = 3
    // 2 + 3 = 5
    private fun task12(a: UInt, b: UInt): String {
        val suma = a + b
        return "$a + $b = $suma"
    }

    // Zdefiniu funkcję, która zwraca wartość logiczną, jeśli parametr `a` jest nieujemny i mniejszy od `b`
    fun task13(a: Double, b: Float): Boolean {
        return a >= 0 && a < b
    }

    // Zdefiniuj funkcję, która zwraca łańcuch dla argumentów całkowitych ze znakiem wg schematu
    // <a> + <b> = <a + b>
    // np. dla parametrów a = 2 i b = 3
    // 2 + 3 = 5
    // jeśli b jest ujemne należy zmienić znak '+' na '-'
    // np. dla a = -2 i b = -5
    //-2 - 5 = -7
    // Wskazówki:
    // Math.abs(a) - zwraca wartość bezwględną
    fun task14(a: Int, b: Int): String {
        val suma = a + b
        return if (b >= 0) {
            "$a + $b = $suma"
        } else {
            "$a - ${Math.abs(b)} = $suma"
        }
    }

    // Zdefiniuj funkcję zwracającą ocenę jako liczbę całkowitą na podstawie łańcucha z opisem słownym oceny.
    // Możliwe przypadki:
    // bardzo dobry 	5
    // dobry 			4
    // dostateczny 		3
    // dopuszczający 	2
    // niedostateczny	1
    // Funkcja nie powinna być wrażliwa na wielkość znaków np. Dobry, DORBRY czy DoBrY to ta sama ocena
    // Wystąpienie innego łańcucha w degree funkcja zwraca wartość -1
    fun task15(degree: String): Int {
        return when (degree.lowercase()) {
            "bardzo dobry" -> 5
            "dobry" -> 4
            "dostateczny" -> 3
            "dopuszczający" -> 2
            "niedostateczny" -> 1
            else -> -1
        }
    }

    // Zdefiniuj funkcję zwracającą liczbę możliwych do zbudowania egzemplarzy, które składają się z elementów umieszczonych w asset
    // Zmienna store jest magazynem wszystkich elementów
    // Przykład
    // store = mapOf("A" to 3, "B" to 4, "C" to 2)
    // asset = mapOf("A" to 1, "B" to 2)
    // var items = task16(store, asset)
    // println(items)	=> 2 ponieważ do zbudowania jednego egzemplarza potrzebne są 2 elementy "B" i jeden "A", a w magazynie mamy 2 "A" i 4 "B",
    // czyli do zbudowania trzeciego egzemplarza zabraknie elementów typu "B"
    fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt {
        var minItems = UInt.MAX_VALUE  // Maksymalna wartość dla UInt

        for ((item, required) in asset) {
            val available = store[item] ?: 0u
            if (available < required) return 0u
            val possible = available / required
            minItems = minOf(minItems, possible)
        }

        return minItems
    }
}