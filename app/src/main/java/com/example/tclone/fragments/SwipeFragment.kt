package com.example.tclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.tclone.R
import com.example.tclone.activities.TinderCallback
import com.example.tclone.adapters.CardsAdapter
import com.example.tclone.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_swipe.*

class SwipeFragment : Fragment() {

    private var callback: TinderCallback? = null
    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var cardsAdapter: ArrayAdapter<User>? = null
    private var rowItems = ArrayList<User>()
    private var preferredGender: String? = null

    fun setCallback(callback: TinderCallback){
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.onGetUserDatabase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDatabase.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                preferredGender = user?.preferredGender
                populateItems()
            }

        })
        cardsAdapter = CardsAdapter(context!!, R.layout.item, rowItems)

        matchFrame.adapter = cardsAdapter
        matchFrame.setFlingListener(object: SwipeFlingAdapterView.onFlingListener{
            override fun removeFirstObjectInAdapter() {
                rowItems.removeAt(0)
                cardsAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                val user = p0 as User
                userDatabase.child(user.uid.toString()).child(DATA_SWIPES_LEFT).child(userId).setValue(true)

            }

            override fun onRightCardExit(p0: Any?) {
                val selectedUser = p0 as User
                val selectedUserId = selectedUser.uid
                if(!selectedUserId.isNullOrEmpty()){
                    userDatabase.child(userId).child(DATA_SWIPES_RIGHT).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError){
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.hasChild(selectedUserId)){
                                    Toast.makeText(context, "Match!", Toast.LENGTH_SHORT).show()

                                    userDatabase.child(userId).child(DATA_SWIPES_RIGHT).child(selectedUserId).removeValue()
                                    userDatabase.child(userId).child(DATA_MATCHES).child(selectedUserId).setValue(true)
                                    userDatabase.child(selectedUserId).child(DATA_MATCHES).child(userId).setValue(true)
                                } else {
                                    userDatabase.child(selectedUserId).child(DATA_SWIPES_RIGHT).child(userId).setValue(true)
                                }
                            }
                        })
                }
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
            }

            override fun onScroll(p0: Float) {
            }

        })

        matchFrame.setOnItemClickListener{ position, data -> //do nothing
        }

        likeButton.setOnClickListener{
            if(!rowItems.isEmpty()){
                matchFrame.topCardListener.selectRight()
            }

        }

        dislikeButton.setOnClickListener{
            if(!rowItems.isEmpty()){
                matchFrame.topCardListener.selectLeft()
            }

        }

    }

    fun populateItems(){
        noUsersLayout.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE

        val cardsQuery = userDatabase.orderByChild(DATA_GENDER).equalTo(preferredGender)
        cardsQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{child ->
                    val user = child.getValue(User::class.java)
                    if(user != null){
                        var showUser = true
                        if(child.child(DATA_SWIPES_LEFT).hasChild(userId) ||
                                child.child(DATA_SWIPES_RIGHT).hasChild(userId) ||
                                child.child(DATA_MATCHES).hasChild(userId) ||
                                user.uid.equals(userId) //this condition removes the current user
                        ){
                            showUser = false
                        }
                        if(showUser){
                            rowItems.add(user)
                            cardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                progressLayout.visibility = View.GONE
                if (rowItems.isEmpty()){
                    noUsersLayout.visibility = View.VISIBLE
                }
            }
        })
    }
}