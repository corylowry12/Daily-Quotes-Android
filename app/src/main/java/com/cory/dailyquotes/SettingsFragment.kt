package com.cory.dailyquotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patchNotesConstraint = view.findViewById<ConstraintLayout>(R.id.constraintPatchNotes)
        patchNotesConstraint.setOnClickListener {
            openFragment(PatchNotesFragment())
        }
    }

    private fun openFragment(fragment: Fragment) {
        val manager =
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        manager.replace(R.id.fragment_container_main, fragment)
            .addToBackStack(null)
        manager.commit()
    }
}