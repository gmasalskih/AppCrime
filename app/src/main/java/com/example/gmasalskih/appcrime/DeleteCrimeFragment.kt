package com.example.gmasalskih.appcrime

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import java.util.*

class DeleteCrimeFragment : DialogFragment() {

    companion object {
        const val TITLE = "title"
        const val ID = "id"
        const val RESULT = "result"
        fun newInstance(title: String, id:UUID): DeleteCrimeFragment {
            val args = Bundle().apply {
                putString(TITLE, title)
                putSerializable(ID, id)
            }
            return DeleteCrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(TITLE)
        val id = arguments?.getSerializable(ID) as UUID
        val v = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_message, null)
        val message: TextView = v.findViewById(R.id.delete_message_dialog)
        message.text = getString(R.string.delete_dialog_message, " ${title?.trim()}")
        return AlertDialog.Builder(activity as Context)
            .setView(v)
            .setTitle(R.string.delete_dialog_title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                sendResult(Activity.RESULT_OK, id)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    private fun sendResult(resultCode: Int, id: UUID) {
        val tf = targetFragment ?: return
        val intent = Intent()
        intent.putExtra(RESULT, id)
        tf.onActivityResult(targetRequestCode, resultCode, intent)

    }
}