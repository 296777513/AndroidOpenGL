package com.example.liyachao.permission

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

object KnightPermission {

    val store: Stack<Activity> = Stack()

    @JvmStatic
    fun getCurActivity(): Activity {
        return store.lastElement()
    }

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {

            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                store.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                store.add(activity)
            }

        })
    }
}