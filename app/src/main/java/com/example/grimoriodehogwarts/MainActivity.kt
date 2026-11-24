
package com.example.grimoriodehogwarts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPersonagens = findViewById<Button>(R.id.btnPersonagens)
        val btnCriaturas = findViewById<Button>(R.id.btnCriaturas)

        btnPersonagens.setOnClickListener {
            val intent = Intent(this, PersonagemActivity::class.java)
            startActivity(intent)
        }

        btnCriaturas.setOnClickListener {
            val intent = Intent(this, CriaturaActivity::class.java)
            startActivity(intent)
        }
    }
}
