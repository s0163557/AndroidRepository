package com.example.app2.ui

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import android.widget.Toast
import com.example.app2.data.Group
import com.example.app2.data.Student
import com.example.app2.databinding.FragmentGroupListBinding
import com.example.app2.databinding.LayoutStudentListelementBinding
import com.example.app2.repository.FacultyRepository
import java.util.UUID


class GroupListFragment(private val group: Group) : Fragment() {
    private var _binding: FragmentGroupListBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var viewModel: GroupListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvGroupList.layoutManager = LinearLayoutManager(context,
                                                    LinearLayoutManager.VERTICAL, false)
        binding.rvGroupList.adapter = GroupListAdapter(group.students?: emptyList())
        viewModel = ViewModelProvider(this).get(GroupListViewModel::class.java)
    }
    
    private var lastItemView : View? = null

    private inner class GroupHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        lateinit var student: Student

        fun bind(student: Student) {
            this.student = student
            var numberOfFeeSeats : Int = 0
            for (i in 0 until student.seatsNumber){
                if (student.occupiedSeats?.get(i) ==false)
                    numberOfFeeSeats+=1
            }
            val s = "Город: ${student.townName}\n Цена: ${student.sale}\n Количество свободных мест: ${numberOfFeeSeats}\n День прибытия: ${student.arrivalDate.day}-${student.arrivalDate.month}-${student.arrivalDate.year + 1900}\n День отправки: ${student.departureDate.day}-${student.departureDate.month}-${student.departureDate.year + 1900}\n"
            itemView.findViewById<TextView>(R.id.tvElement).text = s
            itemView.findViewById<ConstraintLayout>(R.id.constraintButtons).visibility=View.GONE
            itemView.findViewById<ImageButton>(R.id.ibDelete).setOnClickListener {
                showDeleteDialog(student)
            }
            itemView.findViewById<ImageButton>(R.id.ibEdit).setOnClickListener{
                callbacks?.showStudent(group.id,student)
            }
            itemView.findViewById<ImageButton>(R.id.ibOrder).setOnClickListener{
                val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
                builder.setCancelable(true)
                val dialogView = LayoutInflater.from(context).inflate(R.layout.order_input, null)
                builder.setView(dialogView)
                val EmptySeats = dialogView.findViewById(R.id.tvEmptySeats) as TextView
                val ListOfSeats = dialogView.findViewById(R.id.spChooseSeat) as Spinner
                builder.setTitle("Подтверждение заказа")
                var makeString : String = "Свободны следующие места: "
                var adapter = ArrayAdapter<String>(context!!, R.layout.order_input_element, R.id.textView)
                ListOfSeats.adapter = adapter
                for (i in 0 until student.seatsNumber)
                {

                    if (student.occupiedSeats?.get(i) == false)
                    {
                        makeString += (i+1).toString() +", "
                        adapter.add((i+1).toString())
                    }
                }
                makeString+="\n Выберите место:"
                makeString = makeString.dropLast(2)
                EmptySeats.text = makeString

                builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
                    student.occupiedSeats!![ListOfSeats.selectedItem.toString().toInt() - 1] = true
                    Toast.makeText(context, "Место успешно забронировано!", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton(getString(R.string.cancel), null)
                val alert = builder.create()
                alert.show()

            }
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val cl = itemView.findViewById<ConstraintLayout>(R.id.constraintButtons)
            cl.visibility = View.VISIBLE
            lastItemView?.findViewById<ConstraintLayout>(R.id.constraintButtons)?.visibility=View.GONE
            lastItemView = if(lastItemView == itemView) null else itemView
        }
    }

    private fun showDeleteDialog(student: Student) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить рейс в ${student.townName} от ${student.departureDate.date} из списка?")
        builder.setPositiveButton("Подтверждение") {_, _ ->
            viewModel.deleteStudent(group.id,student)
        }
        builder.setNegativeButton("Отмена", null)
        val alert = builder.create()
        alert.show()
    }

    private inner class GroupListAdapter(private val items: List<Student>) :
        RecyclerView.Adapter<GroupHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): GroupHolder {
            val view = layoutInflater.inflate(
                R.layout.layout_student_listelement, parent, false)
            return GroupHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: GroupHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    interface Callbacks{
        fun showStudent(groupID: UUID, _student: Student?)
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