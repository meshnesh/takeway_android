package com.dockyard.myjuice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.pusher.pushnotifications.PushNotifications;
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

private lateinit var recordAdapter: MenuItemAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PushNotifications.start(applicationContext, "instanceID");
        PushNotifications.addDeviceInterest("Text");
    }

    override fun onResume() {
        super.onResume()
        recordAdapter = MenuItemAdapter(this)
        val recordsView = findViewById<View>(R.id.records_view) as ListView
        recordsView.adapter = recordAdapter

        refreshMenuItems()
    }

    private fun refreshMenuItems() {
        val client = AsyncHttpClient()
        client.get("http://10.0.2.2:8080/menu-items", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, response: JSONArray) {
                super.onSuccess(statusCode, headers, response)
                runOnUiThread {
                    val menuItems = IntRange(0, response.length() - 1)
                        .map { index -> response.getJSONObject(index) }
                        .map { obj ->
                            MenuItem(
                                id = obj.getString("id"),
                                name = obj.getString("name")
                            )
                        }

                    recordAdapter.records = menuItems
                }
            }
        })
    }
}
