package com.example.tclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tclone.R
import com.example.tclone.fragments.MatchesFragment
import com.example.tclone.fragments.ProfileFragment
import com.example.tclone.fragments.SwipeFragment
import com.example.tclone.util.DATA_USERS
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*


class TinderActivity : AppCompatActivity(),TinderCallback {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDatabase: DatabaseReference

    private var profileFragment: ProfileFragment? = null
    private var swipeFragment: SwipeFragment? = null
    private var matchesFragment: MatchesFragment? = null

    private var profileTab : TabLayout.Tab? = null
    private var swipeTab : TabLayout.Tab? = null
    private var matchesTab : TabLayout.Tab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(userId.isNullOrEmpty()){
            onSignout()
        }

        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS) //references constants.kt as pointer to schema

        profileTab = navigationTabs.newTab()
        swipeTab = navigationTabs.newTab()
        matchesTab = navigationTabs.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_profile)
        swipeTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_swipe)
        matchesTab?.icon = ContextCompat.getDrawable(this, R.drawable.tab_matches)

        navigationTabs.addTab(profileTab!!)
        navigationTabs.addTab(swipeTab!!)
        navigationTabs.addTab(matchesTab!!)

        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab){
                    profileTab ->{
                        if(profileFragment == null){
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@TinderActivity)
                        }
                        replaceFragment(profileFragment!!)
                    }
                    swipeTab ->{
                        if(swipeFragment == null){
                            swipeFragment = SwipeFragment()
                            swipeFragment!!.setCallback(this@TinderActivity)
                        }
                        replaceFragment(swipeFragment!!)
                    }
                    matchesTab->{
                        if(matchesFragment == null){
                            matchesFragment = MatchesFragment()
                            matchesFragment!!.setCallback(this@TinderActivity)
                        }
                        replaceFragment(matchesFragment!!)
                    }
                }
            }
        })

        profileTab?.select()
    }

    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun onGetUserId(): String = userId!!

    override fun onGetUserDatabase(): DatabaseReference = userDatabase

    companion object{
        fun newIntent(context: Context?) = Intent(context, TinderActivity::class.java)
    }
}