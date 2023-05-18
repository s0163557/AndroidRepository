package com.example.app2.ui

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.app2.R
import com.example.app2.data.Student
import com.example.app2.databinding.FragmentStudentBinding
import java.time.DayOfWeek
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг",
        "Пятница", "Суббота", "Воскресенье")
    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daysOfWeek)
    binding.spDayOfWeek.adapter = adapter
    val PlaneList = listOf(
        "Airbus A319" ,
        "Airbus A320",
        "Airbus A321"
    )
    val adapter2 = ArrayAdapter<String>(requireContext(),
        android.R.layout.simple_spinner_dropdown_item, PlaneList)
    binding.spPlane.adapter = adapter2
    binding.tpDeparture.setIs24HourView(true)
    binding.tpArrival.setIs24HourView(true)
        if (student != null) {
            binding.etTownName.setText(student?.ArrivalCity)
            binding.etSale.setText(student?.Sale.toString()!!)
            binding.spDayOfWeek.setSelection(adapter.getPosition(student!!.DayOfWeek.toString()))
            binding.spPlane.setSelection(adapter2.getPosition(student!!.NameOfPlane))
            binding.tpArrival.hour = student!!.Arrivalhour
            binding.tpArrival.minute = student!!.ArrivalMinute
            binding.tpDeparture.hour = student!!.DepartureJour
            binding.tpDeparture.minute = student!!.DepartureMinute
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
            binding.etSale.text.toString().ifBlank {
                p = false
                binding.etSale.error = "Укажите значение"
            }

            if(p) {

                val dayOfWeekMap = mapOf(
                    "Понедельник" to DayOfWeek.MONDAY,
                    "Вторник" to DayOfWeek.TUESDAY,
                    "Среда" to DayOfWeek.WEDNESDAY,
                    "Четверг" to DayOfWeek.THURSDAY,
                    "Пятница" to DayOfWeek.FRIDAY,
                    "Суббота" to DayOfWeek.SATURDAY,
                    "Воскресенье" to DayOfWeek.SUNDAY
                )

                if (student == null) {
                    student = Student()
                    student?.apply {
                        ArrivalCity = binding.etTownName.text.toString()
                        NameOfPlane = binding.spPlane.selectedItem.toString()
                        Sale = binding.etSale.text.toString().toInt()
                        DepartureJour = binding.tpDeparture.hour
                        DepartureMinute = binding.tpDeparture.minute
                        Arrivalhour = binding.tpArrival.hour
                        ArrivalMinute = binding.tpArrival.minute
                        DayOfWeek = dayOfWeekMap[binding.spDayOfWeek.selectedItem.toString()]!!
                    }
                    viewModel.newStudent(groupID!!, student!!)
                } else {
                    student?.apply {
                        ArrivalCity = binding.etTownName.text.toString()
                        NameOfPlane = binding.spPlane.selectedItem.toString()
                        Sale = binding.etSale.text.toString().toInt()
                        DepartureJour = binding.tpDeparture.hour
                        DepartureMinute = binding.tpDeparture.minute
                        Arrivalhour = binding.tpArrival.hour
                        ArrivalMinute = binding.tpArrival.minute
                       DayOfWeek = dayOfWeekMap[binding.spDayOfWeek.selectedItem.toString()]!!
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