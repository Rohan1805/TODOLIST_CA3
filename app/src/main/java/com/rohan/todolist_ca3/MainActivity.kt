package com.rohan.todolist_ca3

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rohan.todolist_ca3.ui.theme.TODOLIST_CA3Theme

class MainActivity : AppCompatActivity(),UpdateAndDelete {
    lateinit var database:DatabaseReference
    var toDOList:MutableList<Todomodel>?=null
    lateinit var adapter: ToDOAdapter
    private var listViewItem:ListView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab=findViewById<View>(R.id.fab) as FloatingActionButton
        listViewItem=findViewById<ListView>(R.id.list_item_ListView)


        database=FirebaseDatabase.getInstance().reference
        fab.setOnClickListener { view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setMessage("Add ToDO item")
            alertDialog.setTitle("Enter TO DO item")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("ADD") { dialog, i ->
            val todoItemData = Todomodel.createList()
            todoItemData.itemDataText = textEditText.text.toString()
            todoItemData.done = false

            val newItemData = database.child("todo").push()
            todoItemData.UID = newItemData.key

            newItemData.setValue(todoItemData)
            dialog.dismiss()
            Toast.makeText(this, "item saved", Toast.LENGTH_LONG).show()

            alertDialog.show()
        }
        }
        toDOList= mutableListOf<Todomodel>()
        adapter=ToDOAdapter(this,toDOList!!)
        listViewItem!!.adapter=adapter
        database.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "No Item Added",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                toDOList!!.clear()
                addItemToList(snapshot)
            }

        })

    }
    private fun addItemToList(snapshot: DataSnapshot)
    {
        val items=snapshot.children.iterator()

        if(items.hasNext()){
            val todoIndexedValue=items.next()
            val itemsIterator=todoIndexedValue.children.iterator()

            while(itemsIterator.hasNext()){
                val currentItem=itemsIterator.next()
                val todoItemData=Todomodel.createList()
                val map=currentItem.getValue() as HashMap<String,Any>
                todoItemData.UID=currentItem.key
                todoItemData.done=map.get("done") as  Boolean?
                todoItemData.itemDataText=map.get("itemDataText") as String?
                toDOList!!.add(todoItemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItems(itemUID: String, isDone: Boolean) {
        val itemReference=database.child("todo").child(itemUID)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemUID: String) {
        val  itemReference=database.child("todo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()

    }
}




