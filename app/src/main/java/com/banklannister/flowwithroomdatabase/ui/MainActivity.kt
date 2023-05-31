package com.banklannister.flowwithroomdatabase.ui

import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.banklannister.flowwithroomdatabase.R
import com.banklannister.flowwithroomdatabase.adapter.ContactAdapter
import com.banklannister.flowwithroomdatabase.databinding.ActivityMainBinding
import com.banklannister.flowwithroomdatabase.ui.add.AddContactFragment
import com.banklannister.flowwithroomdatabase.ui.deleteall.DeleteAllFragment
import com.banklannister.flowwithroomdatabase.utils.Constants.BUNDLE_ID
import com.banklannister.flowwithroomdatabase.utils.DataStatus
import com.banklannister.flowwithroomdatabase.utils.isVisible
import com.banklannister.flowwithroomdatabase.viewmodel.DatabaseViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var contactAdapter: ContactAdapter
    private val viewModel: DatabaseViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var selectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
//            setSupportActionBar(toolbar)
            btnShowDialog.setOnClickListener {
                AddContactFragment().show(supportFragmentManager, AddContactFragment().tag)
            }
            viewModel.getAllContact()
            viewModel.contactList.observe(this@MainActivity) {
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        loading.isVisible(true, rvContacts)
                        emptyBody.isVisible(false, rvContacts)
                    }

                    DataStatus.Status.SUCCESS -> {
                        it.isEmpty?.let {
                            showEmpty(it)
                        }
                        loading.isVisible(false, rvContacts)
                        contactAdapter.differ.submitList(it.data)
                        rvContacts.apply {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            adapter = contactAdapter
                        }
                    }

                    DataStatus.Status.ERROR -> {
                        loading.isVisible(false, rvContacts)
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionDeleteAll -> {
                        DeleteAllFragment().show(supportFragmentManager, DeleteAllFragment().tag)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.actionSort -> {
                        filter()
                        return@setOnMenuItemClickListener false
                    }

                    R.id.actionSearch -> {
                        return@setOnMenuItemClickListener false
                    }

                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }

            }

            swipeItem()
        }

    }

    private fun swipeItem() {
        val swipeCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val contacts = contactAdapter.differ.currentList[position]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteContacts(contacts)
                        Snackbar.make(binding.root, "Item has been Deleted", Snackbar.LENGTH_LONG)
                            .apply {
                                setAction("Undo") {
                                    viewModel.saveContact(contacts, false)
                                }.show()
                            }
                    }

                    ItemTouchHelper.RIGHT -> {
                        val addContactFragment = AddContactFragment()
                        val bundle = Bundle()
                        bundle.putInt(BUNDLE_ID, contacts.id)
                        addContactFragment.arguments = bundle
                        addContactFragment.show(supportFragmentManager, AddContactFragment().tag)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    //SwipeLeft
                    .addSwipeLeftLabel("Delete")
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .setSwipeLeftActionIconTint(Color.WHITE)
                    //SwipeRight
                    .addSwipeRightLabel("Edit")
                    .addSwipeRightBackgroundColor(Color.GREEN)
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .setSwipeRightLabelColor(Color.WHITE)
                    .setSwipeRightActionIconTint(Color.WHITE)
                    .create()
                    .decorate()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvContacts)

    }

    private fun showEmpty(isShow: Boolean) {
        binding.apply {
            if (isShow) {
                emptyBody.isVisible(true, listBody)
            } else {
                emptyBody.isVisible(false, listBody)
            }
        }
    }

    private fun filter() {
        val builder = AlertDialog.Builder(this)
        val sortItem = arrayOf("Newer (Default", "Name: A-Z", "Name: Z-A")
        builder.setSingleChoiceItems(sortItem, selectedItem) { dialog, item ->
            when (item) {
                0 -> viewModel.getAllContact()
                1 -> viewModel.sortASC()
                2 -> viewModel.sortDESC()
            }

            selectedItem = item
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        val search = menu.findItem(R.id.actionSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Searching..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchContacts(newText!!)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }


}