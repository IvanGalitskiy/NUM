package com.vallsoft.num.ui

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vallsoft.num.R
import com.vallsoft.num.data.database.SettingsPreference
import com.vallsoft.num.presentation.LanguagePresenter
import com.vallsoft.num.presentation.view.ILanguageView
import kotlinx.android.synthetic.main.fragment_language.view.*
import org.intellij.lang.annotations.Language

class LanguageFragment : Fragment(), ILanguageView{

    lateinit var vMessage:TextView
    lateinit var languagePresenter: LanguagePresenter

    enum class Languages {
        EN,RU,DE,JA,PT,FR,IT,ZH
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater!!.inflate(R.layout.fragment_language,container,false)
        languagePresenter = LanguagePresenter.getInstance(SettingsPreference(activity))
        languagePresenter.attach(this)
        v.vEnglish.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.EN.name) }
        v.vRussian.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.RU.name) }
        v.vGermany.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.DE.name) }
        v.vJapan.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.JA.name) }
        v.vPotgugal.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.PT.name) }
        v.vFrance.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.FR.name) }
        v.vItaly.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.IT.name) }
        v.vChina.setOnClickListener { languagePresenter.changeLanguage(activity,Languages.ZH.name) }
        vMessage = v.vMessage
        return v
    }

    override fun onDetach() {
        languagePresenter.detach(this)
        super.onDetach()
    }
    override fun onLanguageChanged() {
        if (view!=null) {
            view.vMessage.text = getString(R.string.select_language)
        }
    }
}