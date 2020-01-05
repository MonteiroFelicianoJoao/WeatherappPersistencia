package br.com.weatherapp.ui.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import br.com.weatherapp.R
import br.com.weatherapp.common.Constants
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {



    private val sp : SharedPreferences by lazy {
        getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        updateUi()
        btnSave.setOnClickListener { save() }
    }

    private fun save() {
        sp.edit {
            putBoolean(Constants.PREFS_TEMP, rbC.isChecked)
            putBoolean(Constants.PREFS_LANG, rbEN.isChecked)
        }
        Toast.makeText(this, getString(R.string.save_settings), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateUi() {
        val isCelsius = sp.getBoolean(Constants.PREFS_TEMP, true)
        val isEnglish = sp.getBoolean(Constants.PREFS_LANG, true)
        rgTemp.check(if (isCelsius) R.id.rbC else R.id.rbF )
        rgLang.check(if (isEnglish) R.id.rbEN else R.id.rbPT)
    }

}
