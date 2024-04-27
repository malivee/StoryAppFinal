package com.belajar.storyapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.belajar.storyapp.data.model.DataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthPreference private constructor(
    private val dataStore: DataStore<Preferences>
) {


    suspend fun saveData(dataModel: DataModel) {
        dataStore.edit {
            it[NAME] = dataModel.name
            it[TOKEN] = dataModel.token
            it[IS_LOGIN] = true

        }
//        editor.putString(NAME, dataModel.name)
//        editor.putString(TOKEN, dataModel.token)
//        editor.putBoolean(IS_LOGIN, dataModel.isLogin)
//            .apply()
    }

    fun getData(): Flow<DataModel> {
//        val dataModel = DataModel()
//        dataModel.name = sharedPreference.getString(NAME, "")
//        dataModel.token = sharedPreference.getString(TOKEN, "")
//        dataModel.isLogin = sharedPreference.getBoolean(IS_LOGIN, true)
//        return dataModel
        return dataStore.data.map {
            DataModel(
                it[NAME] ?: "",
                it[TOKEN] ?: "",
                it[IS_LOGIN] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun getToken(): Flow<String?> {
        return dataStore.data.map {

            it[TOKEN]

        }
    }
    companion object {
        @Volatile
        private var INSTANCE: AuthPreference? = null

        private val NAME = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")
        private val IS_LOGIN = booleanPreferencesKey("isLogin")

//        @Volatile
//        private var INSTANCE: AuthPreference? = null
//        fun getInstance(dataStore: DataStore<Preferences>): AuthPreference =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: AuthPreference(dataStore = dataStore)
//            }.also { INSTANCE = it }

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}


