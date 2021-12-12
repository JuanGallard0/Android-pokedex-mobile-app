package com.naldana.pokedex.ui.pokedex

import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.naldana.pokedex.PokedexApplication
import com.naldana.pokedex.R
import com.naldana.pokedex.data.PokedexDatabase
import com.naldana.pokedex.data.PokedexDatabase_Impl
import com.naldana.pokedex.data.dao.PokemonDao
import com.naldana.pokedex.data.entity.Favorito
import com.naldana.pokedex.data.entity.PokemonType
import com.naldana.pokedex.data.entity.PokemonWithType
import com.naldana.pokedex.repository.PokemonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.*

class PokemonsRecyclerViewAdapter :
    RecyclerView.Adapter<PokemonsRecyclerViewAdapter.PokemonViewHolder>() {

    private var pokemons: List<PokemonWithType>? = null
    private var favoritos: List<Favorito>? = null

    fun setData(pokemons: List<PokemonWithType>) {
        this.pokemons = pokemons
        notifyDataSetChanged()
    }
    fun setFavs(favoritos: List<Favorito>) {
        this.favoritos = favoritos
        notifyDataSetChanged()
    }

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val db = PokedexDatabase.getDatabase(PokedexApplication())

        fun bind(data: PokemonWithType, fav: Boolean) {
            val idTextView = itemView.findViewById<TextView>(R.id.id_pokemon)
            val nameTextView = itemView.findViewById<TextView>(R.id.name_pokemon)
            val imageView = itemView.findViewById<ImageView>(R.id.image_pokemon)
            val favButton = itemView.findViewById<ImageButton>(R.id.fav_button)
            if (fav)
                favButton.setColorFilter(Color.RED)
            else
                favButton.setColorFilter(Color.GRAY)
            setFavorites(favButton, data, fav)
            setTypes(data.types)
            idTextView.text = data.pokemon.id.toString()
            nameTextView.text = data.pokemon.name.capitalize(Locale.ROOT)
            Glide.with(itemView)
                .load(data.pokemon.sprites.frontDefault)
                .centerCrop()
                .placeholder(R.drawable.pokebola)
                .into(imageView)
        }

        private fun setTypes(types: List<PokemonType>) {
            val chips = itemView.findViewById<ChipGroup>(R.id.pokemon_types)
            chips.removeAllViews()
            types.forEach{
                chips.addView(Chip(itemView.context).apply {
                    text = it.type.name.capitalize(Locale.getDefault())
                })
            }
        }

        private fun setFavorites(btn: ImageButton, pokemon: PokemonWithType, fav: Boolean) {
            btn.setOnClickListener {
                if (!fav) {
                    btn.setColorFilter(Color.RED)
                    GlobalScope.launch {
                        db.pokemonDao().insertFav(Favorito(pokemon.pokemon.id))
                    }
                }
                else {
                    btn.setColorFilter(Color.GRAY)
                    GlobalScope.launch {
                        db.pokemonDao().deleteFav(Favorito(pokemon.pokemon.id))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val data = pokemons ?: return
        val pokemon = data[position]
        if (favoritos!!.any{
            it.id == pokemon.pokemon.id
            })
            holder.bind(pokemon,true)
        else
            holder.bind(pokemon,false)
    }

    override fun getItemCount(): Int = pokemons?.size ?: 0
}