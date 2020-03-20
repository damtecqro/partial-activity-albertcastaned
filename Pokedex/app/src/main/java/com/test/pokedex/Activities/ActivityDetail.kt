package com.test.pokedex.Activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.test.pokedex.R
import java.util.*

class ActivityDetail : AppCompatActivity() {
    private var context: Context = this
    private var numero: String = "0"
    private lateinit var data: JsonObject
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var imagePokemon: ImageView
    private lateinit var namePokemon: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        manageIntent()
        initializeComponents()
        initializeData()
    }

    private fun initializeComponents() {
        imagePokemon = findViewById(R.id.pokemon_detail_image)
        namePokemon = findViewById(R.id.pokemon_detail_name)
    }

    private fun manageIntent() {
        if (intent != null)
            numero = intent.getStringExtra("Numero")
    }

    private fun addTextView(innerText: String, parentID: Int) {
        val linearLayout: LinearLayout = findViewById(parentID)

        var type = TextView(this)

        type.gravity = Gravity.CENTER
        type.textSize = 18f
        type.typeface = ResourcesCompat.getFont(this, R.font.roboto)
        type.text = innerText

        linearLayout.addView(type)
    }

    private fun initializeData() {


        Ion.with(context)
            .load("https://pokeapi.co/api/v2/pokemon/" + numero + "/")
            .asJsonObject()
            .done { e, result ->
                if (e == null) {

                    data = result
                    if (!data.get("sprites").isJsonNull) {

                        if (data.get("sprites").asJsonObject.get("front_default") != null) {
                            //Pintar

                            Glide
                                .with(context)
                                .load(data.get("sprites").asJsonObject.get("front_default").asString)
                                .placeholder(R.drawable.pokemon_logo_min)
                                .error(R.drawable.pokemon_logo_min)
                                .into(imagePokemon)

                        } else {

                            imagePokemon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.pokemon_logo_min
                                )
                            )
                        }
                    } else {

                        imagePokemon.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.pokemon_logo_min
                            )
                        )

                        Log.e("JSON", "Sprite null")

                    }

                    if (data.get("name") != null) {
                        val numName: String =
                            "#$numero: " + (this.data.get("name").toString().replace(
                                "\"",
                                ""
                            ).toUpperCase(Locale.ROOT))


                        namePokemon.text = numName
                    } else {

                        namePokemon.text = "NULL"

                    }

                    if (!data.get("types").isJsonNull) {

                        val types = data.get("types").asJsonArray
                        for (i in 0.until(types.size())) {

                            val item = types.get(i).asJsonObject.get("type").asJsonObject

                            val typeName = item.get("name").toString().replace("\"", "")
                                .toUpperCase(Locale.ROOT)

                            addTextView(typeName, R.id.TypesLayout)

                        }

                    } else {
                        Log.e("JSON", "Types null")
                    }

                    if (!data.get("stats").isJsonNull) {
                        val stats = data.get("stats").asJsonArray
                        for (i in 0.until(stats.size())) {
                            val stat_base = stats.get(i).asJsonObject.get("base_stat")
                            val stat_name =
                                stats.get(i).asJsonObject.get("stat").asJsonObject.get("name")

                            val result = stat_name.toString().replace(
                                "\"",
                                ""
                            ).toUpperCase(Locale.ROOT) + ": " + stat_base.toString()
                            addTextView(result, R.id.StatsLayout)

                        }
                    } else {
                        Log.e("JSON", "Stats null")
                    }

                    if (!data.get("moves").isJsonNull) {
                        val moves = data.get("moves").asJsonArray
                        for (i in 0.until(moves.size())) {
                            val move_name =
                                moves.get(i).asJsonObject.get("move").asJsonObject.get("name")

                            val result =
                                move_name.toString().replace("\"", "").toUpperCase(Locale.ROOT)

                            addTextView(result, R.id.MovesLayout)
                        }
                    } else {
                        Log.e("JSON", "Moves null")
                    }


                }
                initializeList()

            }


    }

    private fun initializeList() {
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

    }

}