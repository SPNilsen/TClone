package com.example.tclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.tclone.R
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*


class TinderActivity : AppCompatActivity() {

    private var al = ArrayList<String>()
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        al.add("php")
        al.add("c")
        al.add("python")
        al.add("java")

        //choose your favorite adapter
        arrayAdapter = ArrayAdapter<String>(this,
            R.layout.item,
            R.id.helloText, al)

        frame.adapter = arrayAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!")
                al.removeAt(0)
                arrayAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(this@TinderActivity, "Left!", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                Toast.makeText(this@TinderActivity, "Right!", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Ask for more data here
                al.add("XML " + java.lang.String.valueOf(i))
                arrayAdapter?.notifyDataSetChanged()
                Log.d("LIST", "notified")
                i++
            }

            override fun onScroll(p0: Float) {
            }
        })

    }

    companion object{
        fun newIntent(context: Context?) = Intent(context, TinderActivity::class.java)
    }
}