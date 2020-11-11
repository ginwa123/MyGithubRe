package com.m.ginwa.dicoding.mygithubre.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkBuilder
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.ui.MainActivity
import com.m.ginwa.dicoding.mygithubre.ui.MainActivity.Companion.showToast


class MyFavoriteUsersWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        context?.let {
            if (intent?.action != null) {
                if (intent.action == TOAST_ACTION) {
                    val message = intent.getStringExtra(EXTRA_ITEM)
                    message?.let { it1 -> showToast(context, it1) }
                }
            }
        }
    }

    companion object {
        const val EXTRA_ITEM = "widgetExtraItem"
        private const val TOAST_ACTION = "widgetToastAction"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }
            val views = RemoteViews(context.packageName, R.layout.my_favorite_users_widget).apply {
                setRemoteAdapter(R.id.stackView, intent)
                setEmptyView(R.id.stackView, R.id.emptyView)
            }
            val toastIntent = Intent(context, MyFavoriteUsersWidget::class.java).apply {
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }
            val toastPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setComponentName(MainActivity::class.java)
                .setDestination(R.id.searchUserFragment)
                .createPendingIntent()
            views.setOnClickPendingIntent(R.id.emptyView, pendingIntent)
            views.setPendingIntentTemplate(R.id.stackView, toastPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}
