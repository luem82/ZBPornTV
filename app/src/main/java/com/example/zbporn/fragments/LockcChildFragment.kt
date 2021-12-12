package com.example.zbporn.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zbporn.R
import com.example.zbporn.activities.PasscodeActivity
import com.example.zbporn.utils.PreferenceManager
import kotlinx.android.synthetic.main.fragment_lockc_child.*

class LockcChildFragment : Fragment() {

    private lateinit var preferrances: PreferenceManager
    private var isLock: Boolean? = null
    private var passcode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lockc_child, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferrances = PreferenceManager(requireContext())
        isLock = preferrances.getBoolean("islock")
        passcode = preferrances.getString("passcode")

        if (isLock!!) {
            lockTitle.visibility = View.INVISIBLE
            lockEditText.visibility = View.INVISIBLE
            lockSave.visibility = View.INVISIBLE
            lockOff.visibility = View.VISIBLE
        } else {
            lockTitle.visibility = View.VISIBLE
            lockEditText.visibility = View.VISIBLE
            lockSave.visibility = View.VISIBLE
            lockOff.visibility = View.INVISIBLE
        }

        lockSave.setOnClickListener {
            var pass = lockEditText.text.toString()
            if (pass.isNullOrEmpty()) {
                Toast.makeText(context, "Enter Passcode", Toast.LENGTH_SHORT).show()
            } else {
                preferrances.putBoolean("islock", true)
                preferrances.putString("passcode", pass)
                startActivity(Intent(context, PasscodeActivity::class.java))
                requireActivity().finish()
            }
        }

        lockOff.setOnClickListener {
            preferrances.clear()
            findNavController().navigateUp()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}