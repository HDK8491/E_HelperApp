package com.iohardik.chatapp

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iohardik.chatapp.adapter.UserAdapter
import com.iohardik.chatapp.databinding.ActivityMainBinding
import com.iohardik.chatapp.model.User

class MainActivity : AppCompatActivity() {
    var binding:ActivityMainBinding?=null
    var database:FirebaseDatabase?=null
    var users:ArrayList<User>?=null
    var userAdapter:UserAdapter?=null
  var dialog:ProgressDialog?=null
    var user:User?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog=ProgressDialog(this@MainActivity)
        dialog!!.setMessage("Uploading Image.....")
        dialog!!.setCancelable(false)
        database=FirebaseDatabase.getInstance()
        users= ArrayList<User>()
        userAdapter= UserAdapter(this@MainActivity,users!!)
        val  layoutManager =GridLayoutManager(this@MainActivity,2)
        binding!!.mRec.layoutManager =layoutManager
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
        user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        binding!!.mRec.adapter = userAdapter
        database!!.reference.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              users!!.clear()
                for (snapsort1 in snapshot.children){
                    val user:User?=snapsort1.getValue(User::class.java)
                    if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) users!!.add(user)

                }
                userAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



    }

    override fun onResume() {
        super.onResume()
        val currenId=FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currenId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currenId=FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currenId!!).setValue("Offline")
    }
}