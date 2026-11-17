package com.zone.messanger.text.Fragments

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.viewbinding.ViewBinding
import com.zone.messanger.text.PANDA_Activity.MainLauncherActivity

abstract class MyFragment<BINDING : ViewBinding>(
    context: Context,
    attributeSet: AttributeSet,
) : RelativeLayout(context, attributeSet) {
    protected var activity: MainLauncherActivity? = null
    protected lateinit var binding: BINDING

    abstract fun setupFragment(activity: MainLauncherActivity)
}
