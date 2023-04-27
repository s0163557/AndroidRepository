package com.example.app2.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.Faculty
import com.example.app2.databinding.FragmentFacultyBinding
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

    private inner class FacultyHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        lateinit var faculty: Faculty

        fun bind(faculty: Faculty){
            this.faculty = faculty
            val sb = java.lang.StringBuilder()
            sb.append(faculty.name)
            sb.append(", ")
            sb.append(faculty.yearOfCreate)
            itemView.findViewById<TextView>(R.id.tvFacultyName).text = sb.toString()
        }

        init{
            itemView.setOnClickListener(this)
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

}