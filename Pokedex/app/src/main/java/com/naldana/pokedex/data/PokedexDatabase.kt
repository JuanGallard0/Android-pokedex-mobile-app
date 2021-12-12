package com.naldana.pokedex.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.naldana.pokedex.PokedexApplication
import com.naldana.pokedex.data.dao.PokemonDao
import com.naldana.pokedex.data.entity.Favorito
import com.naldana.pokedex.data.entity.Pokemon
import com.naldana.pokedex.data.entity.PokemonType
import com.naldana.pokedex.ui.pokedex.PokemonsRecyclerViewAdapter

@Database(
    entities = [Pokemon::class, PokemonType::class, Favorito::class],
    version = 1,
    exportSchema = true)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao

    companion object {
        @Volatile
        private var INSTANCE: PokedexDatabase? = null

        fun getDatabase(context: PokedexApplication): PokedexDatabase {
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    PokedexDatabase::class.java, "pokedexDb"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}