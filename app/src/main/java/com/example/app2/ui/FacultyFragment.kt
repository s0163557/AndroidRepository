package com.example.app2.ui

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.Faculty
import com.example.app2.data.Student
import com.example.app2.databinding.FragmentFacultyBinding
import com.example.app2.repository.FacultyRepository
import java.util.*

const val FACULTY_TAG = "FacultyFragment"
const val FACULTY_TITLE = "Авиакомпании"

class FacultyFragment : Fragment() {

    private lateinit var viewModel: FacultyViewModel
    private var _binding : FragmentFacultyBinding? = null
    val binding
        get() = _binding!!

    private var adapter: FacultyListAdapter = FacultyListAdapter(emptyList())

    companion object {
        fun newInstance() = FacultyFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacultyBinding.inflate(inflater, container, false)
        binding.rvFaculty.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FacultyViewModel::class.java)
        viewModel.university.observe(viewLifecycleOwner){
            adapter=FacultyListAdapter(it)
            binding.rvFaculty.adapter = adapter
        }
        callbacks?.setTitle(FACULTY_TITLE)
    }

    var lastItemView : View? = null

    private inner class FacultyHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        lateinit var faculty: Faculty

        fun bind(faculty: Faculty){
            this.faculty = faculty
            val sb = java.lang.StringBuilder()
            sb.append(faculty.name)
            sb.append(", ")
            sb.append(faculty.yearOfCreate.year)
            itemView.findViewById<TextView>(R.id.tvFacultyName).text = sb.toString()
            itemView.findViewById<ImageButton>(R.id.ibDeleteFaculty).setOnClickListener {
                showDeleteDialog(faculty)
            }
            itemView.findViewById<ImageButton>(R.id.ibEditFaculty).setOnClickListener{
                showDateNameInputDialog(faculty)
            }
        }

        init{
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener {
                val cl = itemView.findViewById<ConstraintLayout>(R.id.cbFaculty)
                cl.visibility = View.VISIBLE
                lastItemView?.findViewById<ConstraintLayout>(R.id.cbFaculty)?.visibility=View.GONE
                lastItemView = if(lastItemView == itemView) null else itemView
                true
            }
        }

        override fun onClick (v: View?){
            callbacks?.showGroupFragment(faculty.id)
        }
    }

    private inner class FacultyListAdapter(private val items: List<Faculty>)
        : RecyclerView.Adapter<FacultyHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyHolder {
            val view = layoutInflater.inflate(R.layout.element_faculty_list, parent, false)
            return FacultyHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: FacultyHolder, position: Int) {
            holder.bind(items[position])
        }
    }
    interface Callbacks{
        fun setTitle(_title: String)
        fun showGroupFragment(facultyID : UUID)
    }

    var callbacks : Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }

    private fun showDeleteDialog(faculty: Faculty) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить авиакомпанию ${faculty.name} из списка?")
        builder.setPositiveButton("Подтверждение") {_, _ ->
            viewModel.deleteFaculty(faculty.id)
        }
        builder.setNegativeButton("Отмена", null)
        val alert = builder.create()
        alert.show()
    }

    private fun showDateNameInputDialog(faculty: Faculty) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.date_name_input, null)
        builder.setView(dialogView)
        val nameInput = dialogView.findViewById(R.id.editName2) as EditText
        nameInput.setText(faculty.name)
        val yearInput = dialogView.findViewById(R.id.dtpCalendar2) as DatePicker
        builder.setTitle("Укажите значение")

        yearInput.init(faculty.yearOfCreate.year, faculty.yearOfCreate.month,
            faculty.yearOfCreate.date, null)

        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            val s = nameInput.text.toString()
            val year = yearInput.year
            if (year > 2023)
            {
                Toast.makeText(context, "Год не должен превышать текущий!", Toast.LENGTH_SHORT).show()
            }
            else {
                val Date = Date(yearInput.year, yearInput.month, yearInput.dayOfMonth)
                if (s.isNotBlank() && year != null) {
                    FacultyRepository.get().editFaculty(s, Date, faculty)
                }
            }
        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        val alert = builder.create()
        alert.show()
    }

}