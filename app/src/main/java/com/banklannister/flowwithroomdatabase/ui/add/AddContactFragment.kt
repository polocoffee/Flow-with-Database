package com.banklannister.flowwithroomdatabase.ui.add

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.banklannister.flowwithroomdatabase.databinding.FragmentAddContactBinding
import com.banklannister.flowwithroomdatabase.db.ContactsEntity
import com.banklannister.flowwithroomdatabase.utils.Constants.BUNDLE_ID
import com.banklannister.flowwithroomdatabase.utils.Constants.EDIT
import com.banklannister.flowwithroomdatabase.utils.Constants.NEW
import com.banklannister.flowwithroomdatabase.viewmodel.DatabaseViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddContactFragment : DialogFragment() {

    @Inject
    lateinit var entities: ContactsEntity

    private val viewModel: DatabaseViewModel by viewModels()
    private var contactId = 0
    private var name = ""
    private var phone = ""

    private var type = ""
    private var isEdited = false

    private lateinit var binding: FragmentAddContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater, container, false)

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        contactId = arguments?.getInt(BUNDLE_ID) ?: 0

        if (contactId > 0) {
            type = EDIT
            isEdited = true
        } else {
            type = NEW
            isEdited = false
        }

        binding.apply {
            imgClose.setOnClickListener {
                dismiss()
            }

            if (type == EDIT) {
                viewModel.getContacts(contactId)
                viewModel.contactsDetail.observe(viewLifecycleOwner) { itData ->
                    itData.data?.let {
                        edtName.setText(it.name)
                        edtPhone.setText(it.phone)
                    }
                }
            }

            btnSave.setOnClickListener {
                name = edtName.text.toString()
                phone = edtPhone.text.toString()

                if (name.isEmpty() || phone.isEmpty()) {
                    Snackbar.make(
                        it,
                        "Name or Phone can't be empty!!",
                        Snackbar.ANIMATION_MODE_SLIDE
                    ).show()

                } else {
                    entities.id = contactId
                    entities.name = name
                    entities.phone = phone

                    viewModel.saveContact(entities, isEdited)

                    edtName.setText("")
                    edtPhone.setText("")

                    dismiss()
                }
            }
        }
    }

}