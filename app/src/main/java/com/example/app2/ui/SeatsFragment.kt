package com.example.app2.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.app2.R
import com.example.app2.data.Plane
import com.example.app2.data.Seat
import com.example.app2.databinding.ButtonsListBinding
import java.util.UUID

class SeatsFragment : Fragment() {
    private var _binding: ButtonsListBinding? = null
    private val binding get() = _binding!!

    companion object {
        private lateinit var _plane: Plane
        fun newInstance(plane: Plane?): SeatsFragment {
            _plane = plane!!
            return SeatsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ButtonsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var viewModel: SeatsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SeatsViewModel::class.java)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SeatsAdapter(_plane.seats)
        }
    }

    var PrevSelectedSeat = -1

    private inner class SeatsHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        fun bind(seat: Seat) {
            var Seatname = itemView.findViewById<TextView>(R.id.tvNumberOfSeat)
            val sb = java.lang.StringBuilder()
            sb.append(seat.name)
            Seatname.setText(sb.toString())
            if (seat.isBooked == false)
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            var position: Int = getAdapterPosition()
            if (position == PrevSelectedSeat)
                PrevSelectedSeat = -1
            val seat = _plane.seats[position]
            if (!seat.isBooked) {
                //обработка цветов
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue))
                CommitDialog(seat.name)
                if (PrevSelectedSeat != -1) {
                    var rvSeats = binding.recyclerView
                    var vhSeat: ViewHolder? =
                        rvSeats.findViewHolderForAdapterPosition(PrevSelectedSeat)
                    if (vhSeat != null) {
                        vhSeat.itemView.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.white
                            )
                        )
                    } else {
                        PrevSelectedSeat = -1
                    }
                }
                PrevSelectedSeat = position
            }
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }

        override fun onLongClick(v: View?): Boolean {
            var position: Int = getAdapterPosition()
            val seat = _plane.seats[position]
            _plane.seats.find {
                it.name == seat.name
            }!!.isBooked = false
            binding.recyclerView.adapter?.notifyDataSetChanged()
            return true
        }
    }

    fun CommitDialog(seatname: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setTitle("Забронировать место")
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.commit_dialog, null)
        builder.setView(dialogView)
        //наполнить
        val SeatName = dialogView.findViewById<TextView>(R.id.textView4)
        val sb = java.lang.StringBuilder()
        sb.append(SeatName.text.toString() + " ")
        sb.append(seatname)
        sb.append("?")
        SeatName.setText(sb.toString())
        builder.setPositiveButton("Да") { _, _ ->
            _plane.seats.find {
                it.name == seatname
            }!!.isBooked = true
            binding.recyclerView.adapter?.notifyDataSetChanged()
            Toast.makeText(context, "Вы успешно купили билет $seatname", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Нет", null)
        val alert = builder.create()
        alert.show()
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private inner class SeatsAdapter(private val items: List<Seat>) :
        RecyclerView.Adapter<SeatsFragment.SeatsHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SeatsFragment.SeatsHolder {
            val view = layoutInflater.inflate(R.layout.buttons_list_element, parent, false)
            return SeatsHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: SeatsFragment.SeatsHolder, position: Int) {
            holder.bind(items[position])
        }
    }
}