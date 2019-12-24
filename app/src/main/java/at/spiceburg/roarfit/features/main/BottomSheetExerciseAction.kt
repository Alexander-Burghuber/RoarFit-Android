package at.spiceburg.roarfit.features.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.spiceburg.roarfit.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet_exerciseaction.*

class BottomSheetExerciseAction : BottomSheetDialogFragment() {

    private lateinit var listener: ClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottomsheet_exerciseaction, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as ClickListener
    }

    override fun onStart() {
        super.onStart()
        fab_bottomsheet_qr.setOnClickListener {
            listener.onBottomSheetResult(true)
            dismiss()
        }
        fab_bottomsheet_list.setOnClickListener {
            listener.onBottomSheetResult(false)
            dismiss()
        }
    }

    interface ClickListener {
        fun onBottomSheetResult(useQR: Boolean)
    }
}