package com.example.app2.ui

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.app2.R
import com.example.app2.data.Student
import com.example.app2.databinding.FragmentStudentBinding
import java.util.*

const val STUDENT_TAG = "StudentFragment"

class StudentFragment private constructor() : Fragment() {
    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!
    companion object {
        private var student: Student? = null
        private lateinit var groupID: UUID
        fun newInstance(groupID: UUID, student: Student? = null): StudentFragment {
            this.student = student
            this.groupID = groupID
            return StudentFragment()
        }
    }

    private lateinit var viewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

//    private var selectedDate: Date = Date()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (student != null) {
            binding.etTownName.setText(student?.townName)
            binding.etSeatsNumber.setText(student?.seatsNumber.toString()!!)
            binding.etSale.setText(student?.sale.toString()!!)
            val departureDate = GregorianCalendar().apply {
                time = student!!.departureDate
            }
            val arrivalDate = GregorianCalendar().apply {
                time = student!!.arrivalDate
            }
            binding.dtpDepartureCalendar.init(departureDate.get(Calendar.YEAR), departureDate.get(Calendar.MONTH),
                departureDate.get(Calendar.DAY_OF_MONTH), null)
            binding.dtpArrivalCalendar.init(arrivalDate.get(Calendar.YEAR), arrivalDate.get(Calendar.MONTH),
                arrivalDate.get(Calendar.DAY_OF_MONTH), null)
        }

        viewModel = ViewModelProvider(this).get(StudentViewModel::class.java)
    }
    val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showCommitDialog()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }


    private fun showCommitDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(true)
        builder.setMessage("Сохранить изменения?")
        builder.setTitle("Подтвержение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            var p = true
            binding.etTownName.text.toString().ifBlank {
                p = false
                binding.etTownName.error = "Укажите значение"
            }
            binding.etSeatsNumber.text.toString().ifBlank {
                p = false
                binding.etSeatsNumber.error = "Укажите значение"
            }
            binding.etSale.text.toString().ifBlank {
                p = false
                binding.etSale.error = "Укажите значение"
            }

            if (binding.etSeatsNumber.text.toString().toIntOrNull() ==null)
            {
                p = false
                binding.etSeatsNumber.error = "Значение должно быть числовым!"
            }
            if (binding.etSale.text.toString().toIntOrNull() == null)
            {
                p = false
                binding.etSale.error = "Значение должно быть числовым!"
            }

            val selectedDepartureDate = GregorianCalendar().apply {
                set(GregorianCalendar.YEAR, binding.dtpDepartureCalendar.year)
                set(GregorianCalendar.MONTH, binding.dtpDepartureCalendar.month)
                set(GregorianCalendar.DAY_OF_MONTH, binding.dtpDepartureCalendar.dayOfMonth)
            }

            val selectedArrivalDate = GregorianCalendar().apply {
                set(GregorianCalendar.YEAR, binding.dtpArrivalCalendar.year)
                set(GregorianCalendar.MONTH, binding.dtpArrivalCalendar.month)
                set(GregorianCalendar.DAY_OF_MONTH, binding.dtpArrivalCalendar.dayOfMonth)
            }

            if (selectedDepartureDate.after(selectedArrivalDate))
            {
                p = false
                Toast.makeText(context, "Дата отправки не может быть позже даты прибытия!", Toast.LENGTH_SHORT).show()
            }

            if(p) {

                if (student == null) {
                    student = Student()
                    student?.apply {
                        townName = binding.etTownName.text.toString()
                        seatsNumber = binding.etSeatsNumber.text.toString().toInt()
                        sale = binding.etSale.text.toString().toInt()
                        departureDate = selectedDepartureDate.time
                        arrivalDate = selectedArrivalDate.time
                        for(i in 1..seatsNumber) {
                            occupiedSeats?.add(false)
                        }
                    }
                    viewModel.newStudent(groupID!!, student!!)
                } else {
                    student?.apply {
                        townName = binding.etTownName.text.toString()
                        seatsNumber = binding.etSeatsNumber.text.toString().toInt()
                        sale = binding.etSale.text.toString().toInt()
                        departureDate = selectedDepartureDate.time
                        arrivalDate = selectedArrivalDate.time
                        for(i in 1..seatsNumber){
                            occupiedSeats?.add(false)
                        }
                    }
                    viewModel.editStudent(groupID!!, student!!)
                }
                backPressedCallback.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
            builder.setNegativeButton("Не сохранять") { _, _, ->
                backPressedCallback.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            val alert = builder.create()
            alert.show()

    }




}