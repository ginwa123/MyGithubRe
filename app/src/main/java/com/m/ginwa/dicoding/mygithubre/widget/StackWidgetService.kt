package com.m.ginwa.dicoding.mygithubre.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StackWidgetService : RemoteViewsService() {

    @Inject
    lateinit var stackRemoteViewsFactory: StackRemoteViewsFactory

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = stackRemoteViewsFactory

}
