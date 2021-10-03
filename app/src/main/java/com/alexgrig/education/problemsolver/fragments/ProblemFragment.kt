package com.alexgrig.education.problemsolver.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alexgrig.education.problemsolver.databinding.FragmentProblemBinding
import com.alexgrig.education.problemsolver.entities.Problem
import com.alexgrig.education.problemsolver.viewmodels.ProblemDetailViewModel
import androidx.lifecycle.Observer
import com.alexgrig.education.problemsolver.R
import com.alexgrig.education.problemsolver.utils.StateOfProblem
import java.util.*

class ProblemFragment: Fragment(), DatePickerDialogFragment.Callbacks {

    private lateinit var binding: FragmentProblemBinding
    lateinit var problem: Problem
    private val problemDetailViewModel by viewModels<ProblemDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        problem = Problem()
        val problemId: UUID = arguments?.getSerializable(ARG_PROBLEM_ID) as UUID
        //live data обновятся
        problemDetailViewModel.loadProblem(problemId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProblemBinding.inflate(inflater, container, false)
        binding.waitingState.isChecked = true


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //наблюдаем: если состояние объекта Problem изменилось, перезаписываем глобальный
        //problem и обноаляем вьюху
        problemDetailViewModel.problemLiveData.observe(
            viewLifecycleOwner,
            Observer { problem ->
                problem?.let {
                    this.problem = problem
                    updateUI()
                }
            }
        )
    }

    private fun updateUI() {
        binding.apply {
            problemTitle.setText(problem.title)
            problemDateButton.text = problem.getSimpleDate()
            when (problem.state) {
                StateOfProblem.Waiting -> waitingState.isChecked = true
                StateOfProblem.Solved -> solvedState.isChecked = true
                StateOfProblem.Failed -> failedState.isChecked = true
            }
            if (problem.suspect.isNotEmpty()) {
                chooseSuspectButton.text = problem.suspect
            }
            if (problem.phoneOfSuspect.isNotBlank()) {
                callSuspectButton.isEnabled = true
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // empty
            }
            override fun onTextChanged(sequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                problem.title = sequence.toString()
            }
            override fun afterTextChanged(p0: Editable?) {
                // empty
            }

        }

        binding.problemTitle.addTextChangedListener(titleWatcher)
        binding.radioGroupState.setOnCheckedChangeListener { group, id ->
            onCheckChanged(id)
        }

        binding.problemDateButton.setOnClickListener {
            Toast.makeText(requireContext(), "Tap", Toast.LENGTH_SHORT).show()
        }

        binding.problemDateButton.setOnClickListener {
            DatePickerDialogFragment.newInstance(problem.date).apply {
                setTargetFragment(this@ProblemFragment, REQUEST_DATE_CODE)
                show(this@ProblemFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        binding.chooseSuspectButton.apply {
            val pickContactIntent = contactIntent
            //check available application for contacts
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                if (checkContactsPermission()) {
                    //permission granted, pick contacts
                    startActivityForResult(pickContactIntent, REQUEST_CONTACT)
                } else {
                    //permission not granted, request
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        CONTACT_PERMISSION_CODE
                    )
                }
            }
        }

        binding.sendReportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getProblemReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.problem_report_subject))
            }. also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report_using))
                startActivity(chooserIntent)
            }
        }

        binding.callSuspectButton.apply {
            if (problem.phoneOfSuspect.isBlank()) {
                isEnabled = false
            }
            val callIntent = Intent(Intent.ACTION_DIAL)
            var number = problem.phoneOfSuspect
            callIntent.data = Uri.parse("tel:$number")
            setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(callIntent, REQUEST_PHONE)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), PHONE_PERMISSION_CODE)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //handle permission request result
        when (requestCode) {
            CONTACT_PERMISSION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivityForResult(contactIntent, REQUEST_CONTACT)


                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_permissions), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            PHONE_PERMISSION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:${problem.phoneOfSuspect}")
                    startActivityForResult(callIntent, REQUEST_PHONE)

                } else {
                    Toast.makeText(requireContext(), "Permission denied...", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {

                val contactUri = data?.data ?: return
                // Указать, для каких полей ваш запрос должен возвращать значения.
                val queryFields = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )
                // Выполняемый здесь запрос — contactUri похож на предложение "where"
                val cursor = requireActivity().contentResolver
                    .query(contactUri, queryFields, null, null, null)
                cursor?.use {
                    //Проверить, что курсор содержит хотя бы один результат
                    if (it.count == 0) return

                    // Первый столбец первой строки данных —
                    // это имя вашего подозреваемого.
                    it.moveToFirst()
                    val phoneOfSuspect = it.getString(0)
                    problem.phoneOfSuspect = phoneOfSuspect
                    val suspectName = it.getString(1)
                    problem.suspect = suspectName
                    problemDetailViewModel.saveProblem(problem)
                    binding.chooseSuspectButton.text = suspectName
                }
            }
        }
    }

    private fun checkContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    //обрабатываем события изменения положения радио батона
    private fun onCheckChanged(id: Int) {
        when (id) {
            R.id.waitingState -> problem.state = StateOfProblem.Waiting
            R.id.solvedState -> problem.state = StateOfProblem.Solved
            R.id.failedState -> problem.state = StateOfProblem.Failed
        }
    }
    override fun onDateSelected(date: Date) {
        problem.date = date
        updateUI()
    }


    override fun onStop() {
        super.onStop()
        problemDetailViewModel.saveProblem(problem)
    }


    private fun getProblemReport(): String {

        val stateProblemString = when (problem.state) {
            StateOfProblem.Waiting -> getString(R.string.state_problem_waiting)
            StateOfProblem.Solved -> getString(R.string.state_problem_solved)
            StateOfProblem.Failed -> getString(R.string.state_problem_failed)
        }

        val dateString = DateFormat.format(DATE_FORMAT, problem.date).toString()

        val suspect = if (problem.suspect.isBlank()) {
            getString(R.string.problem_report_no_suspect)
        } else {
            getString(R.string.problem_report_suspect, problem.suspect)
        }

        return getString(R.string.problem_report, problem.title, dateString, stateProblemString, suspect)
    }

    ////////////////////////  STATIC CONTEXT  /////////////////////////////////
    companion object {
        private const val ARG_PROBLEM_ID = "problem id argument"
        private const val DIALOG_DATE = "DialogDate"
        private const val DATE_FORMAT = "EEE, MMM, dd"
        private const val REQUEST_DATE_CODE = 0
        private const val REQUEST_CONTACT = 1
        private const val REQUEST_PHONE = 2
        private const val CONTACT_PERMISSION_CODE = 112
        private const val PHONE_PERMISSION_CODE = 113


        val contactIntent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }

        @JvmStatic
        fun newInstance(problemId: UUID): ProblemFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PROBLEM_ID, problemId)
            }
            return ProblemFragment().apply {
                arguments = args
            }
        }

    }

}